package io.extact.rms.application;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

@SuppressWarnings("deprecation")
@RunWith(JUnitPlatform.class)
@SelectPackages({
    "io.extact.rms.application"
    })
public class AllTestSuite {
}
