/**
 * This package contains simple service for performing database upgrades.
 * 
 * By default supports SQL files with multiple statements. Also can be
 * configured to run custom java code as a part of upgrade process. Default
 * implementation is {@link org.summerb.dbupgrade.impl.DbUpgradeImpl}
 * 
 * In order to simplify bean, create class marked with
 * {@link org.springframework.context.annotation.Configuration} and subclass
 * {@link org.summerb.dbupgrade.DbUpgradeConfigAdapter}
 */
package org.summerb.dbupgrade;