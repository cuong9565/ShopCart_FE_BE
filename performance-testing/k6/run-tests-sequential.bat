@echo off
echo Starting K6 Performance Tests (Sequential)...
echo.

@REM echo Running cart-performance-test.js...
@REM k6 run tests\cart-performance-test.js

echo Running cart-performance-test.js...
k6 run tests\cart-performance-test.js

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
