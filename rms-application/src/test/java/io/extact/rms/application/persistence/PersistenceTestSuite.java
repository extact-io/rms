package io.extact.rms.application.persistence;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

@SuppressWarnings("deprecation")
@RunWith(JUnitPlatform.class)
@SelectPackages({
    "io.extact.rms.application.persistence"
    })
public class PersistenceTestSuite {
}
