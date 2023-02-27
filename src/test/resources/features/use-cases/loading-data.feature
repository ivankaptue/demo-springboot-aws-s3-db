Feature: Loading data from file storage to datastore

  Scenario Outline: Load remote file content in local datastore
    Given Sales data of today available in remote storage with name "<fileName>"
    When The loading process start
    Then Store current file name is saved in datastore with status "<status>"
    And <saleCount> Sales of today are all saved in datastore
    And Products list "<products>" are saved
    Examples:
      | fileName          | status  | saleCount | products                                                                                                                                                                                                                                                   |
      | MAXI20230226.csv  | PENDING | 9         | DURABLE STEEL KEYBOARD,ENORMOUS ALUMINUM TABLE,LIGHTWEIGHT CONCRETE CAR,FANTASTIC LEATHER COAT,SMALL WOOL CLOCK,ERGONOMIC COPPER HAT,GORGEOUS PAPER HAT,GORGEOUS MARBLE CAR,DURABLE BRONZE WATCH                                                           |
      | METRO20230226.csv | PENDING | 12        | DURABLE GRANITE LAMP,INTELLIGENT COTTON TABLE,ENORMOUS WOODEN PLATE,MEDIOCRE PAPER SHOES,RUSTIC WOOL SHOES,ERGONOMIC CONCRETE CAR,AWESOME PAPER KNIFE,ENORMOUS COTTON BENCH,SMALL COPPER CLOCK,SLEEK MARBLE BAG,MEDIOCRE SILK GLOVES,GORGEOUS COPPER BENCH |
