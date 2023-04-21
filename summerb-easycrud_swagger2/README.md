# easycrud_swagger2 overview
[![Maven](https://img.shields.io/maven-central/v/com.github.skarpushin/summerb-easycrud_swagger2)](https://mvnrepository.com/artifact/com.github.skarpushin/summerb-easycrud_swagger2)
[![javadoc](https://javadoc.io/badge2/com.github.skarpushin/summerb-easycrud_swagger2/javadoc.svg)](https://javadoc.io/doc/com.github.skarpushin/summerb-easycrud_swagger2)
[![Join the chat at https://gitter.im/summerb-community/community](https://badges.gitter.im/summerb-community/community.svg)](https://gitter.im/summerb-community/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

This module is very simple - it contains tool for integrating EasyCrud and Swagger2. It is needed only if:

 * You're using summerb-easycrud
 * You're using [HasCommonPathVariable](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/rest/commonpathvars/HasCommonPathVariable.html)
 * And you want Swagger2 to correctly render controller actions and their parameters
 
Make sure to register `PathVariablesMapArgumentResolver` in your MvcConfiguration.
