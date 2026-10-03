# Task 10 — Continuous Testing and the Jenkins Deployment Gate

**Owner:** Kirti Vispute (23102C0078)  
**Date:** 3 October 2026  
**Status:** Jenkins failure/correction demonstrated; final `develop` integration recorded below after PR review

## Objective and changed files

Run the Task 9 Selenium journeys in Jenkins against the JAR built from the checked-out commit. Publish XML and HTML results even when a browser case fails. Deploy the JAR only after all backend and browser tests pass. Demonstrate one deliberately failing Selenium assertion, then a separate correction commit and successful rerun.

The versioned [Jenkinsfile](../Jenkinsfile) now runs **Checkout → Build → Unit Test → Package → Start Application → Selenium Tests → Publish Test Report → Deploy**. [scripts/selenium-app.ps1](../scripts/selenium-app.ps1) starts the newly packaged JAR on `127.0.0.1:8091`, waits for its health endpoint and records source/PID/creation time. Its working directory is `target/selenium-app`, so H2 data stays isolated from the live deployment on 8082. Pipeline `post` stops that exact owned process on either result. Port ownership checks prevent interference with another listener.

The Selenium stage executes five real Chrome WebDriver tests. Its `post` block always records Failsafe XML as Jenkins JUnit results and archives XML, browser screenshots, test-app logs and metadata. When Failsafe fails before the normal report goal, that block renders and archives the HTML report separately. The named **Publish Test Report** stage archives the successful HTML report. A failed Selenium command returns nonzero, so later stages, including **Deploy**, are skipped. The existing `scripts/deploy-local.ps1` still performs owned, health-checked deployment after a passing gate.

## Requirements and reproduction

- Windows Jenkins 2.568.1 at [localhost:8080](http://localhost:8080/) with the existing `physio-portal-pipeline` job reading `Jenkinsfile` from GitHub; JDK21, Maven3, Git and installed Chrome.
- Port 8091 available to the temporary app. Existing deployment parameters remain `APP_ENV=demo/test`, `PORT=8081/8082`. This task used **test/8082**; Jenkins itself uses 8080.
- On the job page choose **Build with Parameters → APP_ENV=test, PORT=8082 → Build**. Before each build, confirm the job SCM branch in Configure: the evidence run used `feature/jenkins-selenium-gate`, and final integration uses `develop`. The saved [feature job configuration](evidence/T10_job_feature_jenkins-selenium-gate.xml) records the actual setting.
- Follow the build's Console Output, Tests and Build Artifacts links. A correct run should show 34 backend cases, five Selenium cases, a 100% HTML report, then a healthy 8082 deployment. An assertion failure should show Maven `BUILD FAILURE`, Jenkins `FAILURE`, an archived report and no Deploy invocation.

The Jenkinsfile runs these commands from its checkout; the 8091 server is started by the PowerShell helper between Package and Selenium Tests:

```text
mvn -B -ntp clean compile
mvn -B -ntp test
mvn -B -ntp -DskipTests package
mvn -B -ntp -Pselenium -DskipUnitTests=true -Dselenium.baseUrl=http://127.0.0.1:8091 verify
mvn -B -ntp -Pselenium surefire-report:failsafe-report-only   (only when a failure needs the HTML report)
```

The first three commands compile, run the 34 isolated backend tests and package the JAR. The fourth tests that exact JAR through the temporary live server; zero exit allows Publish/Deploy, nonzero exit blocks them. The final report-only command renders existing failed XML without rerunning a case. If startup fails, inspect the archived `target/selenium-app/application*.log` and port 8091. If ChromeDriver discovery fails, check Chrome/driver access for the Jenkins service account. If tests fail, inspect the assertion and screenshot rather than weakening it. Selenium logs a Chrome 154/CDP 153 compatibility warning; actual WebDriver journeys ran and their pass/fail results are in the XML.

## Actual deliberate failure — build #4

The first feature commit, `67b5d20f884620c0e5c62d5b33bbdec09c2de8be`, deliberately replaced the registration journey's expected patient name with `TASK10_INTENTIONALLY_WRONG_NAME`. [Jenkins build #4](http://localhost:8080/job/physio-portal-pipeline/4/) checked out that SHA, passed **34/34 backend tests**, started its healthy temporary app, then ran all five browser journeys. Registration failed exactly as designed: expected the wrong name, observed `Selenium Demo registration`; **four browser cases passed, one failed, zero errors/skips**. Maven printed `BUILD FAILURE` and Jenkins finished **FAILURE**.

The console explicitly says **“Stage \"Deploy\" skipped due to earlier failure(s)”**. The failure HTML report, XML and a real 37,216-byte `registration-failure.png` were archived. The test app was stopped. The independently checked live 8082 deployment remained **build #3, source `7f973000a94840c7a91d17e7ce68218df607f3f1`, PID 23396, unchanged JAR SHA256 `4561B7F4BDBCF84BC7ECABCE92ADFD254851197A78BFACCB291041292B708D09`, health UP**. See the [before](evidence/T10_deployment_before.json) and [after](evidence/T10_failed_gate.json) records.

Evidence: [actual failed Jenkins screenshot](../screenshots/T10_failed_pipeline.png), [captured browser failure](../screenshots/T10_failed_registration.png), [console](evidence/T10_console_failed.txt), [build API](evidence/T10_build_failed.json), [JUnit](evidence/T10_tests_failed.json), [Failsafe XML](evidence/T10_failed_selenium.xml), [HTML report](evidence/T10_failed_report/selenium.html), [source/test-app metadata](evidence/T10_failed_test_app.json), and [summary](evidence/T10_failed_summary.json). The failed report was generated in the Selenium stage's `post` block because the named Publish stage correctly did not run after a failure.

## Corrected commit and successful rerun — build #5

The next commit, `8ff53a57bd550fe634a18af38acc2cfe4f043d36`, restored the real `patientName` assertion without changing other browser checks. [Jenkins build #5](http://localhost:8080/job/physio-portal-pipeline/5/) checked out that SHA. It passed **34/34 backend tests and 5/5 browser tests**, with zero failures/errors/skips; the Jenkins test view reports **39 passed**. The HTML report was published, then Deploy ran and Jenkins finished **SUCCESS**.

The independently checked live portal at [localhost:8082](http://localhost:8082/) reported **UP** and two fictional providers after build completion. Deployment metadata names build #5 and the corrected SHA; the archived and live JARs both hash to `17E94AEACCC35A57780321DD78478F2907E0BB82CABB9357CC5574262ED948A2`. The temporary 8091 app was stopped. See the [corrected gate](evidence/T10_corrected_gate.json).

Evidence: [actual success screenshot](../screenshots/T10_successful_rerun.png), [console](evidence/T10_console_corrected.txt), [build API](evidence/T10_build_corrected.json), [Jenkins JUnit](evidence/T10_tests_corrected.json), [Failsafe XML](evidence/T10_corrected_selenium.xml), [HTML report](evidence/T10_corrected_report/selenium.html), [deployment metadata](evidence/T10_corrected_deployment.json), and [summary](evidence/T10_corrected_summary.json).

## Review, final integration and evidence gate

PR/review/merge identifiers and the final `develop` build are recorded here after completion. This is a single-owner project, so a COMMENT self-review is not an independent peer approval. The original deliberately broken commit remains visible in Git history; the corrected branch head is the one reviewed for merging.

Screenshots are actual Jenkins pages after completion. `T10_failed_pipeline.png` shows failed build #4, its revision and one Selenium failure. `T10_successful_rerun.png` shows successful build #5, corrected revision and no test failures. The console and before/after deployment state prove that the failed build did not deploy. Neither screenshot contains credentials.

- [x] Jenkinsfile runs a fresh local app and five Selenium journeys before Deploy
- [x] XML/HTML reports and screenshots archived on success and failure
- [x] Deliberate assertion failure commit and Jenkins FAILURE with Deploy skipped
- [x] Existing live deployment unchanged/healthy after failure
- [x] Separate correction commit, 39 passing Jenkins tests and successful deployment
- [ ] Reviewed PR merged, final `develop` build verified, project records updated
