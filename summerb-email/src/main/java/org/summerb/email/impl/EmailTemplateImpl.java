package org.summerb.email.impl;

import com.google.common.base.Preconditions;
import jakarta.mail.internet.InternetAddress;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.summerb.email.EmailTemplate;
import org.summerb.email.dto.Email;
import org.summerb.email.dto.EmailParameters;
import org.summerb.email.dto.EmailTemplateDto;
import org.summerb.email.dto.InlineResource;
import org.summerb.stringtemplate.api.StringTemplate;
import org.summerb.stringtemplate.api.StringTemplateFactory;
import org.summerb.webappboilerplate.utils.MimeTypeResolver;

/**
 * Impl of EmailTemplate.
 *
 * @author Sergey Karpushin
 */
public class EmailTemplateImpl implements EmailTemplate {
  protected static final InternetAddress[] INET_ADDRESS_ARRAY_TYPE = new InternetAddress[0];

  protected final StringTemplateFactory stringTemplateFactory;
  protected final MimeTypeResolver mimeTypeResolver;

  protected String subjectTemplateText;
  protected String bodyTemplateText;
  protected Map<String, String> images;

  public EmailTemplateImpl(
      EmailTemplateDto emailTemplateDto,
      StringTemplateFactory stringTemplateFactory,
      MimeTypeResolver mimeTypeResolver) {

    Preconditions.checkArgument(
        StringUtils.hasText(emailTemplateDto.getSubjectTemplate()), "Subject template required");
    Preconditions.checkArgument(
        StringUtils.hasText(emailTemplateDto.getBodyTemplate()), "Body template required");

    this.stringTemplateFactory = stringTemplateFactory;
    this.mimeTypeResolver = mimeTypeResolver;

    subjectTemplateText = emailTemplateDto.getSubjectTemplate();
    bodyTemplateText = emailTemplateDto.getBodyTemplate();
    images = emailTemplateDto.getImages();
  }

  @Override
  public Email apply(EmailParameters emailParameters) {
    Preconditions.checkArgument(emailParameters != null, "EmailParameters");
    Preconditions.checkArgument(
        !CollectionUtils.isEmpty(emailParameters.getTo()), "Recipient(s) required");

    StringTemplate subjectTemplate = stringTemplateFactory.build(subjectTemplateText);
    StringTemplate bodyTemplate = stringTemplateFactory.build(bodyTemplateText);

    try {
      Email helper = new Email();
      helper.setTo(emailParameters.getTo().toArray(INET_ADDRESS_ARRAY_TYPE));
      if (!CollectionUtils.isEmpty(emailParameters.getCc())) {
        helper.setCc(emailParameters.getCc().toArray(INET_ADDRESS_ARRAY_TYPE));
      }
      if (!CollectionUtils.isEmpty(emailParameters.getBcc())) {
        helper.setBcc(emailParameters.getBcc().toArray(INET_ADDRESS_ARRAY_TYPE));
      }
      helper.setSubject(subjectTemplate.applyTo(emailParameters.getTemplateParametersObject()));
      helper.setText(bodyTemplate.applyTo(emailParameters.getTemplateParametersObject()));
      if (images != null && !images.isEmpty()) {
        images.forEach(addImages(helper));
      }
      addAttachments(emailParameters, helper);

      return helper;
    } catch (Exception t) {
      throw new IllegalStateException("Failed to build MimeMessage from template", t);
    }
  }

  protected void addAttachments(EmailParameters emailParameters, Email helper) {
    if (emailParameters.getAttachments() == null) {
      return;
    }

    for (Entry<String, byte[]> attachment : emailParameters.getAttachments().entrySet()) {
      helper.addAttachment(attachment.getKey(), new ByteArrayResource(attachment.getValue()));
    }
  }

  protected BiConsumer<String, String> addImages(Email helper) {
    return (imageName, imagePath) -> {
      try {
        byte[] bytes = getClass().getClassLoader().getResourceAsStream(imagePath).readAllBytes();
        helper.addInline(
            imageName,
            new InlineResource(
                new ByteArrayResource(bytes),
                mimeTypeResolver.resolveContentTypeByFileName(imagePath)));
      } catch (Exception e) {
        throw new IllegalStateException(
            "Failed to add image:" + imageName + " from path:" + imagePath + " in email", e);
      }
    };
  }
}
