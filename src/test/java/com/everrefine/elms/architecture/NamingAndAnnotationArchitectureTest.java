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
  static final ArchRule presentationのrequestクラス名はRequestで終わること =
      classes()
          .that()
          .resideInAPackage("..presentation.request..")
          .should()
          .haveSimpleNameEndingWith("Request")
          .as("Rule 8: presentation.requestパッケージのクラス名はRequestで終わること");

  @ArchTest
  static final ArchRule restControllerクラス名はControllerで終わること =
      classes()
          .that()
          .areAnnotatedWith(RestController.class)
          .should()
          .haveSimpleNameEndingWith("Controller")
          .as("Rule 9: @RestController付きクラス名はControllerで終わること");

  @ArchTest
  static final ArchRule applicationServiceインターフェース名はApplicationServiceで終わること =
      classes()
          .that()
          .resideInAPackage("..application.service..")
          .and()
          .areInterfaces()
          .should()
          .haveSimpleNameEndingWith("ApplicationService")
          .as("Rule 10: application.serviceパッケージのインターフェース名はApplicationServiceで終わること");

  @ArchTest
  static final ArchRule applicationService実装クラス名はApplicationServiceImplで終わること =
      classes()
          .that()
          .resideInAPackage("..application.service..")
          .and()
          .areAnnotatedWith(Service.class)
          .should()
          .haveSimpleNameEndingWith("ApplicationServiceImpl")
          .as("Rule 11: application.serviceパッケージの@Serviceクラス名はApplicationServiceImplで終わること");

  @ArchTest
  static final ArchRule applicationのcommandクラス名はCommandで終わること =
      classes()
          .that()
          .resideInAPackage("..application.command..")
          .should()
          .haveSimpleNameEndingWith("Command")
          .as("Rule 12: application.commandパッケージのクラス名はCommandで終わること");

  @ArchTest
  static final ArchRule applicationのdtoクラス名はDtoで終わること =
      classes()
          .that()
          .resideInAPackage("..application.dto..")
          .should()
          .haveSimpleNameEndingWith("Dto")
          .as("Rule 13: application.dtoパッケージのクラス名はDtoで終わること");

  @ArchTest
  static final ArchRule domainRepositoryインターフェース名はRepositoryで終わること =
      classes()
          .that()
          .resideInAPackage("..domain.repository..")
          .and()
          .areInterfaces()
          .should()
          .haveSimpleNameEndingWith("Repository")
          .as("Rule 14: domain.repositoryパッケージのインターフェース名はRepositoryで終わること");

  @ArchTest
  static final ArchRule infrastructureRepository実装クラス名はRepositoryImplで終わること =
      classes()
          .that()
          .resideInAPackage("..infrastructure.repository..")
          .and()
          .areAnnotatedWith(Repository.class)
          .should()
          .haveSimpleNameEndingWith("RepositoryImpl")
          .as("Rule 15: infrastructure.repositoryパッケージの@Repositoryクラス名はRepositoryImplで終わること");

  @ArchTest
  static final ArchRule restControllerクラスにはRequestMappingが付与されていること =
      classes()
          .that()
          .areAnnotatedWith(RestController.class)
          .should()
          .beAnnotatedWith(RequestMapping.class)
          .as("Rule 16: @RestController付きクラスには@RequestMappingが付与されていること");

  @ArchTest
  static final ArchRule restControllerクラスにはSwaggerのTagが付与されていること =
      classes()
          .that()
          .areAnnotatedWith(RestController.class)
          .should()
          .beAnnotatedWith(Tag.class)
          .as("Rule 17: @RestController付きクラスにはSwaggerの@Tagが付与されていること");

  @ArchTest
  static final ArchRule infrastructureRepository実装クラスにはRepositoryが付与されていること =
      classes()
          .that()
          .resideInAPackage("..infrastructure.repository..")
          .and()
          .areNotInterfaces()
          .should()
          .beAnnotatedWith(Repository.class)
          .as("Rule 18: infrastructure.repositoryパッケージの実装クラスには@Repositoryが付与されていること");

  @ArchTest
  static final ArchRule domainServiceインターフェース名はDomainServiceで終わること =
      classes()
          .that()
          .resideInAPackage("..domain.service..")
          .and()
          .areInterfaces()
          .should()
          .haveSimpleNameEndingWith("DomainService")
          .as("Rule 19: domain.serviceパッケージのインターフェース名はDomainServiceで終わること");

  @ArchTest
  static final ArchRule domainService実装クラス名はDomainServiceImplで終わること =
      classes()
          .that()
          .resideInAPackage("..domain.service..")
          .and()
          .areNotInterfaces()
          .should()
          .haveSimpleNameEndingWith("DomainServiceImpl")
          .as("Rule 20: domain.serviceパッケージの実装クラス名はDomainServiceImplで終わること");

  @ArchTest
  static final ArchRule infrastructureのentityクラス名はEntityで終わること =
      classes()
          .that()
          .resideInAPackage("..infrastructure.entity..")
          .should()
          .haveSimpleNameEndingWith("Entity")
          .as("Rule 21: infrastructure.entityパッケージのクラス名はEntityで終わること");

  @ArchTest
  static final ArchRule infrastructureのrowクラス名はRowで終わること =
      classes()
          .that()
          .resideInAPackage("..infrastructure.row..")
          .should()
          .haveSimpleNameEndingWith("Row")
          .as("Rule 22: infrastructure.rowパッケージのクラス名はRowで終わること");
}
