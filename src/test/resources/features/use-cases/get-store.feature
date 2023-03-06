Feature: Get stores

  Scenario: Get saved stores via rest api
    Given Sales data of today available in remote storage with name "METRO20230305.csv"
    And The loading process start
    When Get stores
    Then Saved stores are returned
