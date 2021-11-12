package org.summerb.dbupgrade;

import org.junit.runner.RunWith;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestMySqlConfig.class })
@ProfileValueSourceConfiguration(SystemProfileValueSource.class)
@Transactional
public class DbUpgradeMySqlTest extends DbUpgradeTestBase {
	// NOTE: All impl is in parnt. The only difference is config whether we use
	// MySql or Postgress specifics
}
