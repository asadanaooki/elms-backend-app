package com.everrefine.elms.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = "com.everrefine.elms",
    importOptions = ImportOption.DoNotIncludeTests.class)
class LayerDependencyArchitectureTest {

  private static final String PRESENTATION = "..presentation..";
  private static final String APPLICATION = "..application..";
  private static final String DOMAIN = "..domain..";
  private static final String INFRASTRUCTURE = "..infrastructure..";

  @ArchTest
  static final ArchRule rule01_presentation_must_not_depend_on_infrastructure =
      noClasses()
          .that()
          .resideInAPackage(PRESENTATION)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(INFRASTRUCTURE)
          .as("Rule 1: presentation must not depend on infrastructure");

  @ArchTest
  static final ArchRule rule02_application_must_not_depend_on_infrastructure =
      noClasses()
          .that()
          .resideInAPackage(APPLICATION)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(INFRASTRUCTURE)
          .as("Rule 2: application must not depend on infrastructure");

  @ArchTest
  static final ArchRule rule03_application_must_not_depend_on_presentation =
      noClasses()
          .that()
          .resideInAPackage(APPLICATION)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(PRESENTATION)
          .as("Rule 3: application must not depend on presentation");

  @ArchTest
  static final ArchRule rule04_domain_must_not_depend_on_application =
      noClasses()
          .that()
          .resideInAPackage(DOMAIN)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(APPLICATION)
          .as("Rule 4: domain must not depend on application");

  @ArchTest
  static final ArchRule rule05_domain_must_not_depend_on_infrastructure =
      noClasses()
          .that()
          .resideInAPackage(DOMAIN)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(INFRASTRUCTURE)
          .as("Rule 5: domain must not depend on infrastructure");

  @ArchTest
  static final ArchRule rule06_domain_must_not_depend_on_presentation =
      noClasses()
          .that()
          .resideInAPackage(DOMAIN)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(PRESENTATION)
          .as("Rule 6: domain must not depend on presentation");

  @ArchTest
  static final ArchRule rule07_infrastructure_must_not_depend_on_presentation =
      noClasses()
          .that()
          .resideInAPackage(INFRASTRUCTURE)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(PRESENTATION)
          .as("Rule 7: infrastructure must not depend on presentation");
}
