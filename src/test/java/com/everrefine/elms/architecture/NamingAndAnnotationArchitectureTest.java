package com.everrefine.elms.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AnalyzeClasses(
    packages = "com.everrefine.elms",
    importOptions = ImportOption.DoNotIncludeTests.class)
class NamingAndAnnotationArchitectureTest {

  @ArchTest
  static final ArchRule rule08_requests_must_have_request_suffix =
      classes()
          .that()
          .resideInAPackage("..presentation.request..")
          .should()
          .haveSimpleNameEndingWith("Request")
          .as("Rule 8: presentation request classes must end with Request");

  @ArchTest
  static final ArchRule rule09_rest_controllers_must_have_controller_suffix =
      classes()
          .that()
          .areAnnotatedWith(RestController.class)
          .should()
          .haveSimpleNameEndingWith("Controller")
          .as("Rule 9: @RestController classes must end with Controller");

  @ArchTest
  static final ArchRule rule10_application_service_interfaces_must_have_expected_suffix =
      classes()
          .that()
          .resideInAPackage("..application.service..")
          .and()
          .areInterfaces()
          .should()
          .haveSimpleNameEndingWith("ApplicationService")
          .as("Rule 10: application service interfaces must end with ApplicationService");

  @ArchTest
  static final ArchRule rule11_application_service_implementations_must_have_expected_suffix =
      classes()
          .that()
          .resideInAPackage("..application.service..")
          .and()
          .areAnnotatedWith(Service.class)
          .should()
          .haveSimpleNameEndingWith("ApplicationServiceImpl")
          .as(
              "Rule 11: @Service classes in application.service must end with ApplicationServiceImpl");

  @ArchTest
  static final ArchRule rule12_commands_must_have_command_suffix =
      classes()
          .that()
          .resideInAPackage("..application.command..")
          .should()
          .haveSimpleNameEndingWith("Command")
          .as("Rule 12: application command classes must end with Command");

  @ArchTest
  static final ArchRule rule13_dtos_must_have_dto_suffix =
      classes()
          .that()
          .resideInAPackage("..application.dto..")
          .should()
          .haveSimpleNameEndingWith("Dto")
          .as("Rule 13: application DTO classes must end with Dto");

  @ArchTest
  static final ArchRule rule14_domain_repository_interfaces_must_have_repository_suffix =
      classes()
          .that()
          .resideInAPackage("..domain.repository..")
          .and()
          .areInterfaces()
          .should()
          .haveSimpleNameEndingWith("Repository")
          .as("Rule 14: domain repository interfaces must end with Repository");

  @ArchTest
  static final ArchRule rule15_repository_implementations_must_have_expected_suffix =
      classes()
          .that()
          .resideInAPackage("..infrastructure.repository..")
          .and()
          .areAnnotatedWith(Repository.class)
          .should()
          .haveSimpleNameEndingWith("RepositoryImpl")
          .as(
              "Rule 15: @Repository classes in infrastructure.repository must end with RepositoryImpl");

  @ArchTest
  static final ArchRule rule16_rest_controllers_must_have_request_mapping =
      classes()
          .that()
          .areAnnotatedWith(RestController.class)
          .should()
          .beAnnotatedWith(RequestMapping.class)
          .as("Rule 16: @RestController classes must have @RequestMapping");

  @ArchTest
  static final ArchRule rule17_rest_controllers_must_have_swagger_tag =
      classes()
          .that()
          .areAnnotatedWith(RestController.class)
          .should()
          .beAnnotatedWith(Tag.class)
          .as("Rule 17: @RestController classes must have Swagger @Tag");

  @ArchTest
  static final ArchRule rule18_repository_implementations_must_have_repository_annotation =
      classes()
          .that()
          .resideInAPackage("..infrastructure.repository..")
          .and()
          .areNotInterfaces()
          .should()
          .beAnnotatedWith(Repository.class)
          .as("Rule 18: implementation classes in infrastructure.repository must have @Repository");

  @ArchTest
  static final ArchRule rule19_domain_service_interfaces_must_have_expected_suffix =
      classes()
          .that()
          .resideInAPackage("..domain.service..")
          .and()
          .areInterfaces()
          .should()
          .haveSimpleNameEndingWith("DomainService")
          .as("Rule 19: domain service interfaces must end with DomainService");

  @ArchTest
  static final ArchRule rule20_domain_service_implementations_must_have_expected_suffix =
      classes()
          .that()
          .resideInAPackage("..domain.service..")
          .and()
          .areNotInterfaces()
          .should()
          .haveSimpleNameEndingWith("DomainServiceImpl")
          .as("Rule 20: implementation classes in domain.service must end with DomainServiceImpl");
}
