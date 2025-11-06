package org.summerb.easycrud.sql_builder.mysql;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

public class QueryToSqlMySqlImplTest {

  @Test
  @DisplayName("Should convert simple camelCase to snake_case")
  void testSimpleCamelCase() {
    assertEquals("first_name", QueryToSqlMySqlImpl.snakeCase("firstName"));
    assertEquals("last_name", QueryToSqlMySqlImpl.snakeCase("lastName"));
    assertEquals("email_address", QueryToSqlMySqlImpl.snakeCase("emailAddress"));
  }

  @Test
  @DisplayName("Should handle acronyms at the beginning of words")
  void testAcronymsAtBeginning() {
    assertEquals("url_handler", QueryToSqlMySqlImpl.snakeCase("URLHandler"));
    assertEquals("http_status_code", QueryToSqlMySqlImpl.snakeCase("HTTPStatusCode"));
    assertEquals("xml_parser", QueryToSqlMySqlImpl.snakeCase("XMLParser"));
    assertEquals("html_document", QueryToSqlMySqlImpl.snakeCase("HTMLDocument"));
    assertEquals("pdf_reader", QueryToSqlMySqlImpl.snakeCase("PDFReader"));
  }

  @Test
  @DisplayName("Should handle acronyms in the middle of words")
  void testAcronymsInMiddle() {
    assertEquals("parse_xml_file", QueryToSqlMySqlImpl.snakeCase("parseXMLFile"));
    assertEquals("generate_html_content", QueryToSqlMySqlImpl.snakeCase("generateHTMLContent"));
    assertEquals("handle_http_request", QueryToSqlMySqlImpl.snakeCase("handleHTTPRequest"));
  }

  @Test
  @DisplayName("Should handle acronyms at the end of words")
  void testAcronymsAtEnd() {
    assertEquals("file_url", QueryToSqlMySqlImpl.snakeCase("fileURL"));
    assertEquals("response_xml", QueryToSqlMySqlImpl.snakeCase("responseXML"));
    assertEquals("data_json", QueryToSqlMySqlImpl.snakeCase("dataJSON"));
  }

  @Test
  @DisplayName("Should handle consecutive uppercase letters")
  void testConsecutiveUppercase() {
    assertEquals("user_id", QueryToSqlMySqlImpl.snakeCase("userID"));
    assertEquals("item_list", QueryToSqlMySqlImpl.snakeCase("itemList"));
    assertEquals("test_case", QueryToSqlMySqlImpl.snakeCase("TestCase"));
  }

  @Test
  @DisplayName("Should preserve existing underscores")
  void testPreserveExistingUnderscores() {
    assertEquals("already_snake_case", QueryToSqlMySqlImpl.snakeCase("already_snake_case"));
    assertEquals("with_underscore", QueryToSqlMySqlImpl.snakeCase("with_underscore"));
    assertEquals("mixed_camel_and_snake", QueryToSqlMySqlImpl.snakeCase("mixedCamel_and_snake"));
  }

  @Test
  @DisplayName("Should handle single character words")
  void testSingleCharacterWords() {
    assertEquals("a", QueryToSqlMySqlImpl.snakeCase("A"));
    assertEquals("a", QueryToSqlMySqlImpl.snakeCase("a"));
    assertEquals("id", QueryToSqlMySqlImpl.snakeCase("id"));
  }

  @Test
  @DisplayName("Should handle all caps words")
  void testAllCapsWords() {
    assertEquals("html", QueryToSqlMySqlImpl.snakeCase("HTML"));
    assertEquals("url", QueryToSqlMySqlImpl.snakeCase("URL"));
    assertEquals("http", QueryToSqlMySqlImpl.snakeCase("HTTP"));
    assertEquals("id", QueryToSqlMySqlImpl.snakeCase("ID"));
  }

  @Test
  @DisplayName("Should handle complex mixed cases")
  void testComplexMixedCases() {
    assertEquals(
        "user_http_request_handler", QueryToSqlMySqlImpl.snakeCase("userHTTPRequestHandler"));
    assertEquals("xml_to_json_converter", QueryToSqlMySqlImpl.snakeCase("XMLToJSONConverter"));
    assertEquals(
        "parse_multipart_form_data", QueryToSqlMySqlImpl.snakeCase("parseMultipartFormData"));
    assertEquals(
        "handle_ssl_tls_connection", QueryToSqlMySqlImpl.snakeCase("handleSSL_TLSConnection"));
  }

  @Test
  @DisplayName("Should handle numbers in field names")
  void testNumbersInFieldNames() {
    assertEquals("version2", QueryToSqlMySqlImpl.snakeCase("version2"));
    assertEquals("api_version3", QueryToSqlMySqlImpl.snakeCase("apiVersion3"));
    assertEquals("item2d_array", QueryToSqlMySqlImpl.snakeCase("item2DArray"));
    assertEquals("http2_protocol", QueryToSqlMySqlImpl.snakeCase("HTTP2Protocol"));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Should handle null and empty strings")
  void testNullAndEmpty(String input) {
    assertEquals("", QueryToSqlMySqlImpl.snakeCase(input));
  }

  @ParameterizedTest
  @CsvSource({
    "simple, simple",
    "camelCase, camel_case",
    "URLHandler, url_handler",
    "HTTPStatusCode, http_status_code",
    "XMLParser, xml_parser",
    "userID, user_id",
    "itemList, item_list",
    "already_snake, already_snake",
    "TestClass, test_class",
    "HTMLDocument, html_document"
  })
  @DisplayName("Parameterized test for various cases")
  void testParameterizedCases(String input, String expected) {
    assertEquals(expected, QueryToSqlMySqlImpl.snakeCase(input));
  }

  @Test
  @DisplayName("Should handle field names with multiple acronyms")
  void testMultipleAcronyms() {
    assertEquals("ssl_tls_http_handler", QueryToSqlMySqlImpl.snakeCase("SSL_TLS_HTTPHandler"));
    assertEquals("xmljson_converter", QueryToSqlMySqlImpl.snakeCase("XMLJSONConverter"));
  }

  @Test
  @DisplayName("Should handle very long camelCase names")
  void testLongCamelCaseNames() {
    String input = "thisIsAVeryLongCamelCaseFieldNameWithMultipleWords";
    String expected = "this_is_a_very_long_camel_case_field_name_with_multiple_words";
    assertEquals(expected, QueryToSqlMySqlImpl.snakeCase(input));
  }

  @Test
  @DisplayName("Should handle special character patterns")
  void testSpecialCharacterPatterns() {
    assertEquals("abc", QueryToSqlMySqlImpl.snakeCase("ABC"));
    assertEquals("ab_c", QueryToSqlMySqlImpl.snakeCase("AbC"));
    assertEquals("a_bc", QueryToSqlMySqlImpl.snakeCase("ABc"));
  }

  @Test
  @DisplayName("Should handle field names with underscores and camelCase mixed")
  void testMixedUnderscoreAndCamelCase() {
    assertEquals("user_profile_data", QueryToSqlMySqlImpl.snakeCase("userProfile_data"));
    assertEquals("api_config_settings", QueryToSqlMySqlImpl.snakeCase("apiConfig_settings"));
    assertEquals("http_request_log", QueryToSqlMySqlImpl.snakeCase("HTTPRequest_log"));
  }

  @Test
  @DisplayName("Should not modify already correct snake_case")
  void testAlreadyCorrectSnakeCase() {
    String[] alreadyCorrect = {
      "first_name", "last_name", "email_address", "phone_number",
      "created_at", "updated_at", "is_active", "user_id"
    };

    for (String input : alreadyCorrect) {
      assertEquals(
          input,
          QueryToSqlMySqlImpl.snakeCase(input),
          "Should not modify already correct snake_case: " + input);
    }
  }
}
