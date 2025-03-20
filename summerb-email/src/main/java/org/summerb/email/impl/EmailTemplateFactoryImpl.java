package org.summerb.email.impl;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.function.Supplier;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.summerb.email.EmailTemplate;
import org.summerb.email.EmailTemplateFactory;
import org.summerb.email.dto.EmailTemplateDto;
import org.summerb.stringtemplate.api.StringTemplateFactory;
import org.summerb.webappboilerplate.utils.MimeTypeResolver;

public class EmailTemplateFactoryImpl implements EmailTemplateFactory {
  protected final Logger log = LoggerFactory.getLogger(getClass());

  protected static final String LOCALE_PART_SEPARATOR = "-";
  protected static final String NAME_LOCALE_SEPARATOR = "_";

  protected static final String EXTENSION_HTML = ".html";
  protected static final String EXTENSION_JSON = ".json";

  protected final ResourceLoader resourceLoader;
  protected final StringTemplateFactory stringTemplateFactory;
  protected final MimeTypeResolver mimeTypeResolver;
  protected final String templatesBasePath;
  protected final Gson gson;

  protected Supplier<Locale> defaultLocaleSupplier = () -> Locale.ENGLISH;

  public EmailTemplateFactoryImpl(
      StringTemplateFactory stringTemplateFactory,
      MimeTypeResolver mimeTypeResolver,
      String templatesBasePath,
      Gson gson,
      ResourceLoader resourceLoader) {
    Preconditions.checkNotNull(stringTemplateFactory, "stringTemplateFactory required");
    Preconditions.checkNotNull(mimeTypeResolver, "mimeTypeResolver required");
    Preconditions.checkNotNull(gson, "gson required");
    Preconditions.checkArgument(
        StringUtils.hasText(templatesBasePath), "templatesBasePath required");
    Preconditions.checkNotNull(resourceLoader, "resourceLoader required");
    this.resourceLoader = resourceLoader;
    this.stringTemplateFactory = stringTemplateFactory;
    this.mimeTypeResolver = mimeTypeResolver;
    this.templatesBasePath = templatesBasePath;
    this.gson = gson;
  }

  @Override
  public EmailTemplate build(String templateName, Locale locale) {
    try {
      Preconditions.checkArgument(
          StringUtils.hasText(templateName), "Email template name requried");

      EmailTemplateDto emailTemplateDto = loadTemplateHeader(templateName, locale);
      loadBodyTemplate(emailTemplateDto, templateName, locale);

      return instantiate(emailTemplateDto);
    } catch (Exception t) {
      throw new IllegalStateException("Failed to build EmailTemplate " + templateName, t);
    }
  }

  protected EmailTemplateImpl instantiate(EmailTemplateDto emailTemplateDto) {
    return new EmailTemplateImpl(emailTemplateDto, stringTemplateFactory, mimeTypeResolver);
  }

  protected void loadBodyTemplate(
      EmailTemplateDto emailTemplateDto, String templateName, Locale requestedLocale)
      throws IOException {
    Pair<Locale, InputStream> templateBody =
        getTemplateStream(templateName, requestedLocale, EXTENSION_HTML);
    String bodyJson = readToString(templateBody.getValue(), StandardCharsets.UTF_8);
    emailTemplateDto.setBodyTemplate(bodyJson);
  }

  protected EmailTemplateDto loadTemplateHeader(String templateName, Locale requestedLocale)
      throws IOException {
    Pair<Locale, InputStream> templateHeader =
        getTemplateStream(templateName, requestedLocale, EXTENSION_JSON);
    String headerJson = readToString(templateHeader.getValue(), StandardCharsets.UTF_8);
    EmailTemplateDto emailTemplateDto = gson.fromJson(headerJson, EmailTemplateDto.class);
    emailTemplateDto.setLocale(templateHeader.getKey());
    return emailTemplateDto;
  }

  protected String readToString(InputStream in, Charset charset) throws IOException {
    byte[] bytes = StreamUtils.copyToByteArray(in);
    return new String(bytes, charset);
  }

  protected Pair<Locale, InputStream> getTemplateStream(
      String templateName, Locale requestedLocale, String extension) throws IOException {
    Locale locale = requestedLocale != null ? requestedLocale : defaultLocaleSupplier.get();

    Pair<Locale, String> locationInfo =
        buildFileName3(templatesBasePath, templateName, locale, extension);
    InputStream stream = getResourceInputStream(locationInfo.getValue());
    if (stream != null) {
      return Pair.of(locationInfo.getKey(), stream);
    }

    locationInfo = buildFileName2(templatesBasePath, templateName, locale, extension);
    stream = getResourceInputStream(locationInfo.getValue());
    if (stream != null) {
      return Pair.of(locationInfo.getKey(), stream);
    }

    locationInfo = buildFileName1(templatesBasePath, templateName, locale, extension);
    stream = getResourceInputStream(locationInfo.getValue());
    if (stream != null) {
      return Pair.of(locationInfo.getKey(), stream);
    }

    // We didn't find language-based template. Thus we'll search for default
    // template
    String location = templatesBasePath + "/" + templateName + extension;
    stream = getResourceInputStream(location);
    log.debug(
        "Expecting default-locale location for template {} present at this location: {}",
        templateName,
        location);
    if (stream != null) {
      return Pair.of(defaultLocaleSupplier.get(), stream);
    }

    throw new IllegalArgumentException(
        "Email template with name "
            + templateName
            + extension
            + " cannot be found in base folder "
            + templatesBasePath);
  }

  protected InputStream getResourceInputStream(String pathname) throws IOException {
    Resource template = resourceLoader.getResource(pathname);
    if (!template.exists()) {
      return null;
    }
    return template.getInputStream();
  }

  /**
   * @return returns file name based on all 2 components of the locale: Language only
   */
  protected Pair<Locale, String> buildFileName1(
      String templatesBaseFolder, String templateName, Locale locale, String extension) {
    Locale localeOption = new Locale(locale.getLanguage());
    String path =
        templatesBaseFolder + "/" + templateName + NAME_LOCALE_SEPARATOR + locale.getLanguage();
    path += extension;
    return Pair.of(localeOption, path);
  }

  /**
   * @return returns file name based on all 2 components of the locale: Language and Country
   */
  protected Pair<Locale, String> buildFileName2(
      String templatesBaseFolder, String templateName, Locale locale, String extension) {
    Locale ret = new Locale(locale.getLanguage(), locale.getCountry());
    String path =
        templatesBaseFolder + "/" + templateName + NAME_LOCALE_SEPARATOR + locale.getLanguage();
    if (StringUtils.hasText(locale.getCountry())) {
      path += LOCALE_PART_SEPARATOR + locale.getCountry();
    }
    path += extension;
    return Pair.of(ret, path);
  }

  /**
   * @return returns file name based on all 3 components of the locale: Language, Country and
   *     Variant
   */
  protected Pair<Locale, String> buildFileName3(
      String templatesBaseFolder, String templateName, Locale locale, String extension) {
    Locale ret = new Locale(locale.getLanguage(), locale.getCountry(), locale.getVariant());
    String path =
        templatesBaseFolder + "/" + templateName + NAME_LOCALE_SEPARATOR + locale.getLanguage();
    if (StringUtils.hasText(locale.getCountry())) {
      path += LOCALE_PART_SEPARATOR + locale.getCountry();
      if (StringUtils.hasText(locale.getVariant())) {
        path += LOCALE_PART_SEPARATOR + locale.getVariant();
      }
    }
    path += extension;
    return Pair.of(ret, path);
  }

  public Supplier<Locale> getDefaultLocaleSupplier() {
    return defaultLocaleSupplier;
  }

  public void setDefaultLocaleSupplier(Supplier<Locale> defaultLocaleSupplier) {
    this.defaultLocaleSupplier = defaultLocaleSupplier;
  }
}
