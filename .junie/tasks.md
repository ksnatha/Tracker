1. Make this project as a simple spring boot project - Done
2. Update the pom as needed to include the dependencies - Done
3. Once the basic setup is done, run the project and verify the application is up and running. - Done
4. Introduce a simple test controller and expose an API "hello" which is sends back a sring message "hello" - Done
5. Run the project again and make sue whether you are able to access this api - Done
6. Create a new module to handle emails. Will add the code later - Done
7. Create a new module to handle audit history. Will add the code later - Done
8. Create a new module to handle dashboards. Will add the code later - Done
9. Create a new module to handle workflows. Will add the code later. - Done
10. Update the pom and add the module dependencies and include these modules as part of build. - Done
11. Rebuild the project, run the test and start the application and hit the hello api. - Done
12. Rename package name in all modules. Instead of com.example, use com.odyssey - Done
13. We are going to work on workflow module - Done
14. Workflow module is using Camunda BPMN. So add the dependencies in pom.xml- Done
15. I am planning to create a BPM workflow using Camunda Modeler and add the xml in the project resources folder later.- Done
16. In the meantime, lets finish some coding part. - Done
17. This workflow service should be generic in nature. Once the workflow status of the tracker record and complete workflow is finalized, we will use this service.- Done
18. Workflow name is Tracker-core-workflow-v1- Done
19. Now create a service method in the workflow module to start the workflow, complete the flow, assign task to the user, complete the task to the user.
20. Assume all are user based on task and need to assign more than one user to the task- Done
21. Sometimes we need to assign the users to the task after task is created.- Done
22. Rebuild the project, run the test and start the application and hit the hello api. If any errors are there fix it. - Done
23. Add the flyway dependency - Done
24. Create the SQL for the entity classes present in the workflow module. Postgres will be used. - Done
25. Follow the flyway naming convention for these SQL files - Done
