"# challenge" 

## Feature implemented:

1. End point to transfer of money between accounts is created. Request payload to transfer money includes following attributes:
* accountFrom id
* accountTo id
* amount to transfer between accounts

2. Have covered various error scenarios.

3. Have used the NotificationService interface to communicate with both account holders, with a message containing the account no. of the other account and amount transferred.

4. Have implemented test case for new transfer end point.

5. Have implemented mock for the NotificationService interface and verified the method call in both test class i.e. AccountsControllerTest.java & AccountsControllerTest.java
 
