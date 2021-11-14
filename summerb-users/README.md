# SummerB Users
[![Maven](https://img.shields.io/maven-central/v/com.github.skarpushin/summerb-users)](https://mvnrepository.com/artifact/com.github.skarpushin/summerb-users)
[![javadoc](https://javadoc.io/badge2/com.github.skarpushin/summerb-users/javadoc.svg)](https://javadoc.io/doc/com.github.skarpushin/summerb-users)
[![Join the chat at https://gitter.im/summerb-community/community](https://badges.gitter.im/summerb-community/community.svg)](https://gitter.im/summerb-community/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## What SummerB Users library is offering
Few building blocks usually needed in multi-user environment to manage user accounts and their access. 

## Capabilities
Users library has following building blocks implemented:
 * Users
 * Permissions
 * Passwords
 * Auth tokens

## Tested with databases
 * MariaDB (MySQL)
 * Postgress

## Usage
Elaborative description on how to use it is yet to come. For now please rely on javadocs and sources to understand usage approach. Key parts:
 * [UserService](https://www.javadoc.io/doc/com.github.skarpushin/summerb-users/latest/org/summerb/users/api/UserService.html)
 * [PasswordService](https://www.javadoc.io/doc/com.github.skarpushin/summerb-users/latest/org/summerb/users/api/PasswordService.html)
 * [PermissionService](https://www.javadoc.io/doc/com.github.skarpushin/summerb-users/latest/org/summerb/users/api/PermissionService.html)
 * [AuthTokenService](https://www.javadoc.io/doc/com.github.skarpushin/summerb-users/latest/org/summerb/users/api/AuthTokenService.html)

Btw, `PermissionService` is a **very** flexible way of managing individual permissions (including ACL-type of permissions, no hierarchies though). Can be used even without rest of User service. 
