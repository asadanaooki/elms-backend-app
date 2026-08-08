package com.everrefine.elms.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
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
  static void presentationのrequestクラス名がRequestで終わること(JavaClasses classes) {
    classes()
        .that()
        .resideInAPackage("..presentation.request..")
        .should()
        .haveSimpleNameEndingWith("Request")
        .as("presentation.requestパッケージのクラス名がRequestで終わること")
        .check(classes);
  }

  @ArchTest
  static void restControllerクラス名がControllerで終わること(JavaClasses classes) {
    classes()
        .that()
        .areAnnotatedWith(RestController.class)
        .should()
        .haveSimpleNameEndingWith("Controller")
        .as("@RestController付きクラス名がControllerで終わること")
        .check(classes);
  }

  @ArchTest
  static void applicationServiceインターフェース名がApplicationServiceで終わること(JavaClasses classes) {
    classes()
        .that()
        .resideInAPackage("..application.service..")
        .and()
        .areInterfaces()
        .should()
        .haveSimpleNameEndingWith("ApplicationService")
        .as("application.serviceパッケージのインターフェース名がApplicationServiceで終わること")
        .check(classes);
  }

  @ArchTest
  static void applicationService実装クラス名がApplicationServiceImplで終わること(JavaClasses classes) {
    classes()
        .that()
        .resideInAPackage("..application.service..")
        .and()
        .areAnnotatedWith(Service.class)
        .should()
        .haveSimpleNameEndingWith("ApplicationServiceImpl")
        .as("application.serviceパッケージの@Serviceクラス名がApplicationServiceImplで終わること")
        .check(classes);
  }

  @ArchTest
  static void applicationのcommandクラス名がCommandで終わること(JavaClasses classes) {
    classes()
        .that()
        .resideInAPackage("..application.command..")
        .should()
        .haveSimpleNameEndingWith("Command")
        .as("application.commandパッケージのクラス名がCommandで終わること")
        .check(classes);
  }

  @ArchTest
  static void applicationのdtoクラス名がDtoで終わること(JavaClasses classes) {
    classes()
        .that()
        .resideInAPackage("..application.dto..")
        .should()
        .haveSimpleNameEndingWith("Dto")
        .as("application.dtoパッケージのクラス名がDtoで終わること")
        .check(classes);
  }

  @ArchTest
  static void domainRepositoryインターフェース名がRepositoryで終わること(JavaClasses classes) {
    classes()
        .that()
        .resideInAPackage("..domain.repository..")
        .and()
        .areInterfaces()
        .should()
        .haveSimpleNameEndingWith("Repository")
        .as("domain.repositoryパッケージのインターフェース名がRepositoryで終わること")
        .check(classes);
  }

  @ArchTest
  static void infrastructureRepository実装クラス名がRepositoryImplで終わること(JavaClasses classes) {
    classes()
        .that()
        .resideInAPackage("..infrastructure.repository..")
        .and()
        .areAnnotatedWith(Repository.class)
        .should()
        .haveSimpleNameEndingWith("RepositoryImpl")
        .as("infrastructure.repositoryパッケージの@Repositoryクラス名がRepositoryImplで終わること")
        .check(classes);
  }

  @ArchTest
  static void restControllerクラスにRequestMappingが付与されていること(JavaClasses classes) {
    classes()
        .that()
        .areAnnotatedWith(RestController.class)
        .should()
        .beAnnotatedWith(RequestMapping.class)
        .as("@RestController付きクラスに@RequestMappingが付与されていること")
        .check(classes);
  }

  @ArchTest
  static void restControllerクラスにSwaggerのTagが付与されていること(JavaClasses classes) {
    classes()
        .that()
        .areAnnotatedWith(RestController.class)
        .should()
        .beAnnotatedWith(Tag.class)
        .as("@RestController付きクラスにSwaggerの@Tagが付与されていること")
        .check(classes);
  }

  @ArchTest
  static void infrastructureRepository実装クラスにRepositoryが付与されていること(JavaClasses classes) {
    classes()
        .that()
        .resideInAPackage("..infrastructure.repository..")
        .and()
        .areNotInterfaces()
        .should()
        .beAnnotatedWith(Repository.class)
        .as("infrastructure.repositoryパッケージの実装クラスに@Repositoryが付与されていること")
        .check(classes);
  }

  @ArchTest
  static void domainServiceインターフェース名がDomainServiceで終わること(JavaClasses classes) {
    classes()
        .that()
        .resideInAPackage("..domain.service..")
        .and()
        .areInterfaces()
        .should()
        .haveSimpleNameEndingWith("DomainService")
        .as("domain.serviceパッケージのインターフェース名がDomainServiceで終わること")
        .check(classes);
  }

  @ArchTest
  static void domainService実装クラス名がDomainServiceImplで終わること(JavaClasses classes) {
    classes()
        .that()
        .resideInAPackage("..domain.service..")
        .and()
        .areNotInterfaces()
        .should()
        .haveSimpleNameEndingWith("DomainServiceImpl")
        .as("domain.serviceパッケージの実装クラス名がDomainServiceImplで終わること")
        .check(classes);
  }

  @ArchTest
  static void infrastructureのentityクラス名がEntityで終わること(JavaClasses classes) {
    classes()
        .that()
        .resideInAPackage("..infrastructure.entity..")
        .should()
        .haveSimpleNameEndingWith("Entity")
        .as("infrastructure.entityパッケージのクラス名がEntityで終わること")
        .check(classes);
  }

  @ArchTest
  static void infrastructureのrowクラス名がRowで終わること(JavaClasses classes) {
    classes()
        .that()
        .resideInAPackage("..infrastructure.row..")
        .should()
        .haveSimpleNameEndingWith("Row")
        .as("infrastructure.rowパッケージのクラス名がRowで終わること")
        .check(classes);
  }
}
