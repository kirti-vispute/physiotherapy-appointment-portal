package com.kirtivispute.physio.selenium;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.LifecycleMethodExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

/** Capture the browser before teardown; never swallow the original failure. */
final class FailureScreenshotExtension implements TestExecutionExceptionHandler, LifecycleMethodExecutionExceptionHandler {
    private final SeleniumSupport support;
    FailureScreenshotExtension(SeleniumSupport support) { this.support = support; }

    @Override public void handleTestExecutionException(ExtensionContext context, Throwable error) throws Throwable {
        capture(context, error); throw error;
    }
    @Override public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable error) throws Throwable {
        capture(context, error); throw error;
    }
    private void capture(ExtensionContext context, Throwable error) {
        try { support.saveEvidence(context.getRequiredTestMethod().getName() + "-failure", error.toString()); }
        catch (Exception captureError) { error.addSuppressed(captureError); }
    }
}
