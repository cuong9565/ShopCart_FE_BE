@echo off
echo Starting K6 Performance Tests (Sequential)...
echo.

:: Run test files one by one
echo Running cart-performance-test.js...
k6 run tests\cart-performance-test.js
if %ERRORLEVEL% EQU 0 (
    echo Cart Test: SUCCESS
    set CART_RESULT=SUCCESS
) else (
    echo Cart Test: FAILED - Error Code: %ERRORLEVEL%
    set CART_RESULT=FAILED
)
echo ========================================
echo.

echo Running order-performance-test.js...
k6 run tests\order-performance-test.js
if %ERRORLEVEL% EQU 0 (
    echo Order Test: SUCCESS
    set ORDER_RESULT=SUCCESS
) else (
    echo Order Test: FAILED - Error Code: %ERRORLEVEL%
    set ORDER_RESULT=FAILED
)
echo ========================================
echo.

echo Running checkout-performance-test.js...
k6 run tests\checkout-performance-test.js
if %ERRORLEVEL% EQU 0 (
    echo Checkout Test: SUCCESS
    set CHECKOUT_RESULT=SUCCESS
) else (
    echo Checkout Test: FAILED - Error Code: %ERRORLEVEL%
    set CHECKOUT_RESULT=FAILED
)
echo ========================================
echo.

echo Running stress-test.js...
k6 run tests\stress-test.js
if %ERRORLEVEL% EQU 0 (
    echo Stress Test: SUCCESS
    set STRESS_RESULT=SUCCESS
) else (
    echo Stress Test: FAILED - Error Code: %ERRORLEVEL%
    set STRESS_RESULT=FAILED
)
echo ========================================
echo.

echo All tests completed.
echo Summary:
echo - Cart Test: %CART_RESULT%
echo - Order Test: %ORDER_RESULT%
echo - Checkout Test: %CHECKOUT_RESULT%
echo - Stress Test: %STRESS_RESULT%
echo.

echo Results files created:
echo - cart-test-results.json
echo - order-test-results.json
echo - checkout-test-results.json
echo - stress-test-results.json
pause
