## dbhelper

### stucture

1. Created specific classes for each entities, like User, Category, etc instead of lists of String for easier use

2. Select methods will convert Cursor to objects of these classes (multiple records will become a list) and return

3. Update methods also accept these objects. typically, update something is like: 
     * use select to get some objects
    * use setter to change their values, 
    * then pass the object to update methods as parameter

4. Delete just need id

### type

1. Money records use BigDecimal type now (avoid precision loss like float / double)

   *  When storing into db, BigDecimal is converted to long (long integer) in java, and then become INTEGER in db
   
   * in java code, money values are processed using BigDecimal methods, like 
       * *a = a + b* is like *a = a.add(b)*, 
       * *a = -a* is like *a = a.negate()*
       * *a < b* is like *a.compareTo(b) < 0*

2. Date records use LocalDate

   * When storing into db, they are converted to String, then stored as TEXT type

3. All convert methods are in util/Converter class

## base activity / base fragment

1. have references for global resources so all activity / fragment can use them directly

    * db:           database helper
    * preference:   shared preference (used for read values)
    * editor:       shared preference (used for write values in it)
    * currentUserId:    currently logged in user id (int)
    * currentUser       currently logged in User object
    * currentOverview   current user's Overview object

## navigation

1. for going back to the previous page, use onBackPressed()

2. for going to another page but not allowing to use back button (like cannot go back from main page to login page), add CLEAR_TASK | NEW_TASK flags to intents. 




