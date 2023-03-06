Feature: Get sales

  Scenario: Get saved sales via rest api
    Given Sales data of today available in remote storage with name "METRO20230305.csv"
    And The loading process start
    And Store current file name is saved in datastore with status "PENDING"
    When Get sales
    Then Saved sales are returned
      | product                  | quantity | price    |
      | DURABLE GRANITE LAMP     | 130      | 13120.33 |
      | INTELLIGENT COTTON TABLE | 45       | 1803.89  |
