## Tests Completed ##
  * Building Unit Tests
  * CategoryItem Unit Tests
  * FINMap Unit Tests
  * FINMenu Unit Tests
  * FINMenu Activity Tests
  * FINUtil Unit Tests
  * Get Unit Tests
  * JsonParser Unit Tests

## Tests Needed ##
  * CategoryList Activity Tests

## Tests Out Of Scope ##
  * Create Tests (Do not want to automate database population)
  * FINAdd Activity Tests (Same reason as above)
  * FINHelp Tests (Just a documentation page)
  * FINSplash Tests (Just a splash screen)
  * FINMap Activity Tests (Cannot automate map testing in any elegant fashion)
  * PopUpDialog Tests (Not unit-testable or separate activity)
  * Update Tests (Do not want to automate database population)

All out of scope tests are being manually tested.

## Current Test Results ##
  * 40 / 40 Tests Completed Successfully

## How To Run Test Package ##
  1. Begin by adding the FindItNow project into Eclipse and setting up the emulator, etc as needed for development (I assume there will be separate instructions for this...)
  1. Click File -> Import and under General, select "Existing Projects into Workspace"
  1. Click Browse next to Select Root Directory, and navigate to the tests folder in the FindItNow project
  1. Click Finish (A new Android Test Project will appear in the Package Explorer along the left)
  1. Right Click on "FindItNowTest", choose Run As, Android JUnit Test

## Backend Tests Completed ##
  * Create unit tests
  * getLocations unit tests
  * getCategories unit tests
  * getBuildings unit tests
  * update unit tests

All automated tests are being run on a test database that is
being deleted and recreated on load. Not running on the live
version, but has the same attributes.
## Backend Tests Out of Scope ##
  * deleteNotFound

All out of scope tests are being manually tested.

## Current Backend Test Results ##
  * 32/32

## How To Run Automated Backend Tests ##
  1. go to this link http://cubist.cs.washington.edu/projects/11wi/cse403/RecycleLocator/test/test.php
  1. Hit run tests and tests will run
  1. If you wish to run the tests a second time you must refresh the page