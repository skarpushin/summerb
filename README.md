# SummerB
Kick-start now and Evolve later your Java back-end application!

[![Maven](https://img.shields.io/maven-central/v/com.github.skarpushin/summerb-easycrud)](https://mvnrepository.com/search?q=summerb)
[![javadoc](https://javadoc.io/badge2/com.github.skarpushin/summerb-easycrud/javadoc.svg)](https://javadoc.io/doc/com.github.skarpushin/summerb-easycrud)
[![License](https://img.shields.io/hexpm/l/plug)](LICENSE.txt)
[![Join the chat at https://gitter.im/summerb-community/community](https://badges.gitter.im/summerb-community/community.svg)](https://gitter.im/summerb-community/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# What SummerB is offering?
A set of building blocks that are usually needed for java back-end applications. With those building blocks it gives you a head start - allows to quickly assemble application. 

it is based on Spring Framework and designed using [SOLID](https://www.slideshare.net/skarpushin/solid-ood-dry) design principles. 
Since 2015, summerb  was used in a number of commercial and personal projects, proven to serve its purpose. Since then it was improved number of times. 

Some of the mostly used libraries are:
 * [*EasyCrud*](summerb-easycrud/README.md) - JdbcTemplate-based CRUD functionality designed for simplicity and extensibility
 * [*Users*](summerb-users/README.md) - implementation of repositories for Users and Permissions (suitable for ACL style)
 * [*Validation*](summerb-validation/README.md) - a library designed for business-logic validation (most appropriate for service layer, NOT facade)
 * [*DB Upgrade*](summerb-dbupgrade/README.md) - Simplistic approach for migrating your DB (way simpler than flyway and liquibase)
 * [*Internationalization (i18n)*](summerb-i18n/README.md) - elementary primitives for user language agnostic messages

A bit more rare, but still sometimes useful things:
 * [*Security*](summerb-security/README.md) - elementary security primitives on top of Spring Security
 * [*Properties*](summerb-properties/README.md) - a persistent approach for handling business-objects properties (don't confuse with application properties)
 * [*StringTemplate*](summerb-stringtemplate/README.md) - primitives for compiling string templates and evaluating them (default impl is based on SpEL)
 * [*MiniCMS*](summerb-minicms/README.md) - gives you locale-based functionality for working with text content
 * [*Email*](summerb-email/README.md) - primitives for sending emails
 * [*Webboilerplate*](summerb-webboilerplate/README.md) - a set of heavily opinionated parts of Spring MVC application (I think I will tear down this soon)

# Designed for evolution
Unlike some other tools, SummerB allows you to quickly bootstrap usual facilities in your application *AND allow you to evolve your application while gradually augmenting or replacing SummerB components*. 

If you're working in software development world long enough, you know the price of "magic". At some point you come across some library/framework that allows you to "implement" huge amount of functionality in a matter of few keystrokes. But then (usually this happens in real, long-living projects, used by real people) you bump into some shortage - a bottleneck that significantly slows you down and does not allow you to grow. 

SummerB is designed to overcome this. You can do both -- quickly bootstrap the application here and now AND evolve it to meet business needs later. 
