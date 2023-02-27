package com.klid.s3db.acceptance.config;

import com.klid.s3db.Application;
import com.klid.s3db.model.Constants;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@CucumberContextConfiguration
@SpringBootTest(
  classes = {Application.class, AcceptanceTestConfig.class},
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@ActiveProfiles({Constants.PROFILE_INTEGRATION_TEST})
public @interface AcceptanceSpringContext {
}
