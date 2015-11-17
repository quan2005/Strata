/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.market.value;

import static com.opengamma.strata.basics.currency.Currency.GBP;
import static com.opengamma.strata.basics.currency.Currency.USD;
import static com.opengamma.strata.basics.date.DayCounts.ACT_365F;
import static com.opengamma.strata.collect.TestHelper.assertThrowsIllegalArg;
import static com.opengamma.strata.collect.TestHelper.coverBeanEquals;
import static com.opengamma.strata.collect.TestHelper.coverImmutableBean;
import static com.opengamma.strata.collect.TestHelper.date;
import static com.opengamma.strata.market.value.CompoundedRateType.CONTINUOUS;
import static com.opengamma.strata.market.value.CompoundedRateType.PERIODIC;
import static org.testng.Assert.assertEquals;

import java.time.LocalDate;

import org.testng.annotations.Test;

import com.opengamma.strata.basics.interpolator.CurveInterpolator;
import com.opengamma.strata.collect.array.DoubleArray;
import com.opengamma.strata.market.Perturbation;
import com.opengamma.strata.market.curve.Curve;
import com.opengamma.strata.market.curve.CurveMetadata;
import com.opengamma.strata.market.curve.CurveName;
import com.opengamma.strata.market.curve.CurveUnitParameterSensitivities;
import com.opengamma.strata.market.curve.Curves;
import com.opengamma.strata.market.curve.DefaultCurveMetadata;
import com.opengamma.strata.market.curve.InterpolatedNodalCurve;
import com.opengamma.strata.market.interpolator.CurveInterpolators;
import com.opengamma.strata.market.sensitivity.ZeroRateSensitivity;

/**
 * Test {@link ZeroRateDiscountFactors}.
 */
@Test
public class ZeroRateDiscountFactorsTest {

  private static final LocalDate DATE_VAL = date(2015, 6, 4);
  private static final LocalDate DATE_AFTER = date(2015, 7, 30);

  private static final CurveInterpolator INTERPOLATOR = CurveInterpolators.LINEAR;
  private static final CurveName NAME = CurveName.of("TestCurve");
  private static final CurveMetadata METADATA = Curves.zeroRates(NAME, ACT_365F);

  private static final InterpolatedNodalCurve CURVE =
      InterpolatedNodalCurve.of(METADATA, DoubleArray.of(0, 10), DoubleArray.of(1, 2), INTERPOLATOR);
  private static final InterpolatedNodalCurve CURVE2 =
      InterpolatedNodalCurve.of(METADATA, DoubleArray.of(0, 10), DoubleArray.of(2, 3), INTERPOLATOR);

  private static final double SPREAD = 0.05;
  private static final double TOL = 1.0e-12;
  private static final double EPS = 1.0e-6;

  //-------------------------------------------------------------------------
  public void test_of() {
    ZeroRateDiscountFactors test = ZeroRateDiscountFactors.of(GBP, DATE_VAL, CURVE);
    assertEquals(test.getCurrency(), GBP);
    assertEquals(test.getValuationDate(), DATE_VAL);
    assertEquals(test.getCurve(), CURVE);
    assertEquals(test.getCurveName(), NAME);
    assertEquals(test.getParameterCount(), 2);
  }

  public void test_of_badCurve() {
    InterpolatedNodalCurve notYearFraction = InterpolatedNodalCurve.of(
        Curves.prices(NAME), DoubleArray.of(0, 10), DoubleArray.of(1, 2), INTERPOLATOR);
    InterpolatedNodalCurve notZeroRate = InterpolatedNodalCurve.of(
        Curves.discountFactors(NAME, ACT_365F), DoubleArray.of(0, 10), DoubleArray.of(1, 2), INTERPOLATOR);
    CurveMetadata noDayCountMetadata = DefaultCurveMetadata.builder()
        .curveName(NAME)
        .xValueType(ValueType.YEAR_FRACTION)
        .yValueType(ValueType.ZERO_RATE)
        .build();
    InterpolatedNodalCurve notDayCount = InterpolatedNodalCurve.of(
        noDayCountMetadata, DoubleArray.of(0, 10), DoubleArray.of(1, 2), INTERPOLATOR);
    assertThrowsIllegalArg(() -> ZeroRateDiscountFactors.of(GBP, DATE_VAL, notYearFraction));
    assertThrowsIllegalArg(() -> ZeroRateDiscountFactors.of(GBP, DATE_VAL, notZeroRate));
    assertThrowsIllegalArg(() -> ZeroRateDiscountFactors.of(GBP, DATE_VAL, notDayCount));
  }

  //-------------------------------------------------------------------------
  public void test_discountFactor() {
    ZeroRateDiscountFactors test = ZeroRateDiscountFactors.of(GBP, DATE_VAL, CURVE);
    double relativeYearFraction = ACT_365F.relativeYearFraction(DATE_VAL, DATE_AFTER);
    double expected = Math.exp(-relativeYearFraction * CURVE.yValue(relativeYearFraction));
    assertEquals(test.discountFactor(DATE_AFTER), expected);
  }

  //-------------------------------------------------------------------------
  public void test_discountFactorWithSpread_continuous() {
    ZeroRateDiscountFactors test = ZeroRateDiscountFactors.of(GBP, DATE_VAL, CURVE);
    double relativeYearFraction = ACT_365F.relativeYearFraction(DATE_VAL, DATE_AFTER);
    double expected = Math.exp(-relativeYearFraction * (CURVE.yValue(relativeYearFraction) + SPREAD));
    assertEquals(test.discountFactorWithSpread(DATE_AFTER, SPREAD, CONTINUOUS, 0), expected, TOL);
  }

  public void test_discountFactorWithSpread_periodic() {
    int periodPerYear = 4;
    ZeroRateDiscountFactors test = ZeroRateDiscountFactors.of(GBP, DATE_VAL, CURVE);
    double relativeYearFraction = ACT_365F.relativeYearFraction(DATE_VAL, DATE_AFTER);
    double discountFactorBase = test.discountFactor(DATE_AFTER);
    double rate = (Math.pow(discountFactorBase, -1d / periodPerYear / relativeYearFraction) - 1d) * periodPerYear;
    double expected = discountFactorFromPeriodicallyCompoundedRate(rate + SPREAD, periodPerYear, relativeYearFraction);
    assertEquals(test.discountFactorWithSpread(DATE_AFTER, SPREAD, PERIODIC, periodPerYear), expected, TOL);
  }

  public void test_discountFactorWithSpread_smallYearFraction() {
    ZeroRateDiscountFactors test = ZeroRateDiscountFactors.of(GBP, DATE_VAL, CURVE);
    assertEquals(test.discountFactorWithSpread(DATE_VAL, SPREAD, PERIODIC, 1), 1d, TOL);
  }

  private double discountFactorFromPeriodicallyCompoundedRate(double rate, double periodPerYear, double time) {
    return Math.pow(1d + rate / periodPerYear, -periodPerYear * time);
  }

  //-------------------------------------------------------------------------
  public void test_zeroRatePointSensitivity() {
    ZeroRateDiscountFactors test = ZeroRateDiscountFactors.of(GBP, DATE_VAL, CURVE);
    double relativeYearFraction = ACT_365F.relativeYearFraction(DATE_VAL, DATE_AFTER);
    double df = Math.exp(-relativeYearFraction * CURVE.yValue(relativeYearFraction));
    ZeroRateSensitivity expected = ZeroRateSensitivity.of(GBP, DATE_AFTER, -df * relativeYearFraction);
    assertEquals(test.zeroRatePointSensitivity(DATE_AFTER), expected);
  }

  public void test_zeroRatePointSensitivity_sensitivityCurrency() {
    ZeroRateDiscountFactors test = ZeroRateDiscountFactors.of(GBP, DATE_VAL, CURVE);
    double relativeYearFraction = ACT_365F.relativeYearFraction(DATE_VAL, DATE_AFTER);
    double df = Math.exp(-relativeYearFraction * CURVE.yValue(relativeYearFraction));
    ZeroRateSensitivity expected = ZeroRateSensitivity.of(GBP, DATE_AFTER, USD, -df * relativeYearFraction);
    assertEquals(test.zeroRatePointSensitivity(DATE_AFTER, USD), expected);
  }

  //-------------------------------------------------------------------------
  public void test_zeroRatePointSensitivityWithSpread_continous() {
    ZeroRateDiscountFactors test = ZeroRateDiscountFactors.of(GBP, DATE_VAL, CURVE);
    double relativeYearFraction = ACT_365F.relativeYearFraction(DATE_VAL, DATE_AFTER);
    double df = Math.exp(-relativeYearFraction * (CURVE.yValue(relativeYearFraction) + SPREAD));
    ZeroRateSensitivity expected = ZeroRateSensitivity.of(GBP, DATE_AFTER, -df * relativeYearFraction);
    assertEquals(test.zeroRatePointSensitivityWithSpread(DATE_AFTER, SPREAD, CONTINUOUS, 0), expected);
  }

  public void test_zeroRatePointSensitivityWithSpread_sensitivityCurrency_continous() {
    ZeroRateDiscountFactors test = ZeroRateDiscountFactors.of(GBP, DATE_VAL, CURVE);
    double relativeYearFraction = ACT_365F.relativeYearFraction(DATE_VAL, DATE_AFTER);
    double df = Math.exp(-relativeYearFraction * (CURVE.yValue(relativeYearFraction) + SPREAD));
    ZeroRateSensitivity expected = ZeroRateSensitivity.of(GBP, DATE_AFTER, USD, -df * relativeYearFraction);
    assertEquals(test.zeroRatePointSensitivityWithSpread(DATE_AFTER, USD, SPREAD, CONTINUOUS, 0), expected);
  }

  public void test_zeroRatePointSensitivityWithSpread_periodic() {
    int periodPerYear = 4;
    ZeroRateDiscountFactors test = ZeroRateDiscountFactors.of(GBP, DATE_VAL, CURVE);
    double relativeYearFraction = ACT_365F.relativeYearFraction(DATE_VAL, DATE_AFTER);
    double discountFactorUp = Math.exp(-(CURVE.yValue(relativeYearFraction) + EPS) * relativeYearFraction);
    double discountFactorDw = Math.exp(-(CURVE.yValue(relativeYearFraction) - EPS) * relativeYearFraction);
    double rateUp = (Math.pow(discountFactorUp, -1d / periodPerYear / relativeYearFraction) - 1d) * periodPerYear;
    double rateDw = (Math.pow(discountFactorDw, -1d / periodPerYear / relativeYearFraction) - 1d) * periodPerYear;
    double expectedValue = 0.5 / EPS * (
        discountFactorFromPeriodicallyCompoundedRate(rateUp + SPREAD, periodPerYear, relativeYearFraction) -
        discountFactorFromPeriodicallyCompoundedRate(rateDw + SPREAD, periodPerYear, relativeYearFraction));
    ZeroRateSensitivity computed = test.zeroRatePointSensitivityWithSpread(
        DATE_AFTER, SPREAD, PERIODIC, periodPerYear);
    assertEquals(computed.getSensitivity(), expectedValue, EPS);
    assertEquals(computed.getCurrency(), GBP);
    assertEquals(computed.getCurveCurrency(), GBP);
    assertEquals(computed.getDate(), DATE_AFTER);
  }

  public void test_zeroRatePointSensitivityWithSpread_sensitivityCurrency_periodic() {
    int periodPerYear = 4;
    ZeroRateDiscountFactors test = ZeroRateDiscountFactors.of(GBP, DATE_VAL, CURVE);
    double relativeYearFraction = ACT_365F.relativeYearFraction(DATE_VAL, DATE_AFTER);
    double discountFactorUp = Math.exp(-(CURVE.yValue(relativeYearFraction) + EPS) * relativeYearFraction);
    double discountFactorDw = Math.exp(-(CURVE.yValue(relativeYearFraction) - EPS) * relativeYearFraction);
    double rateUp = (Math.pow(discountFactorUp, -1d / periodPerYear / relativeYearFraction) - 1d) * periodPerYear;
    double rateDw = (Math.pow(discountFactorDw, -1d / periodPerYear / relativeYearFraction) - 1d) * periodPerYear;
    double expectedValue = 0.5 / EPS * (
        discountFactorFromPeriodicallyCompoundedRate(rateUp + SPREAD, periodPerYear, relativeYearFraction) -
        discountFactorFromPeriodicallyCompoundedRate(rateDw + SPREAD, periodPerYear, relativeYearFraction));
    ZeroRateSensitivity computed = test
        .zeroRatePointSensitivityWithSpread(DATE_AFTER, USD, SPREAD, PERIODIC, periodPerYear);
    assertEquals(computed.getSensitivity(), expectedValue, EPS);
    assertEquals(computed.getCurrency(), USD);
    assertEquals(computed.getCurveCurrency(), GBP);
    assertEquals(computed.getDate(), DATE_AFTER);
  }

  public void test_zeroRatePointSensitivityWithSpread_smallYearFraction() {
    ZeroRateDiscountFactors test = ZeroRateDiscountFactors.of(GBP, DATE_VAL, CURVE);
    ZeroRateSensitivity expected = ZeroRateSensitivity.of(GBP, DATE_VAL, -0d);
    assertEquals(test.zeroRatePointSensitivityWithSpread(DATE_VAL, SPREAD, CONTINUOUS, 0), expected);
  }

  public void test_zeroRatePointSensitivityWithSpread_sensitivityCurrency_smallYearFraction() {
    ZeroRateDiscountFactors test = ZeroRateDiscountFactors.of(GBP, DATE_VAL, CURVE);
    ZeroRateSensitivity expected = ZeroRateSensitivity.of(GBP, DATE_VAL, USD, -0d);
    assertEquals(test.zeroRatePointSensitivityWithSpread(DATE_VAL, USD, SPREAD, PERIODIC, 2), expected);
  }

  //-------------------------------------------------------------------------
  public void test_unitParameterSensitivity() {
    ZeroRateDiscountFactors test = ZeroRateDiscountFactors.of(GBP, DATE_VAL, CURVE);
    double relativeYearFraction = ACT_365F.relativeYearFraction(DATE_VAL, DATE_AFTER);
    CurveUnitParameterSensitivities expected = CurveUnitParameterSensitivities.of(
        CURVE.yValueParameterSensitivity(relativeYearFraction));
    assertEquals(test.unitParameterSensitivity(DATE_AFTER), expected);
  }

  //-------------------------------------------------------------------------
  // proper end-to-end FD tests are elsewhere
  public void test_curveParameterSensitivity() {
    ZeroRateDiscountFactors test = ZeroRateDiscountFactors.of(GBP, DATE_VAL, CURVE);
    ZeroRateSensitivity point = ZeroRateSensitivity.of(GBP, DATE_AFTER, 1d);
    assertEquals(test.curveParameterSensitivity(point).size(), 1);
  }

  //-------------------------------------------------------------------------
  public void test_applyPerturbation() {
    Perturbation<Curve> perturbation = curve -> CURVE2;
    ZeroRateDiscountFactors test = ZeroRateDiscountFactors.of(GBP, DATE_VAL, CURVE).applyPerturbation(perturbation);
    assertEquals(test.getCurve(), CURVE2);
  }

  public void test_withCurve() {
    ZeroRateDiscountFactors test = ZeroRateDiscountFactors.of(GBP, DATE_VAL, CURVE).withCurve(CURVE2);
    assertEquals(test.getCurve(), CURVE2);
  }

  //-------------------------------------------------------------------------
  public void coverage() {
    ZeroRateDiscountFactors test = ZeroRateDiscountFactors.of(GBP, DATE_VAL, CURVE);
    coverImmutableBean(test);
    ZeroRateDiscountFactors test2 = ZeroRateDiscountFactors.of(USD, DATE_VAL.plusDays(1), CURVE2);
    coverBeanEquals(test, test2);
  }

}
