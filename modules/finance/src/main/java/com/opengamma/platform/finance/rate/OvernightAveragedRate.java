/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.platform.finance.rate;

import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.ImmutableValidator;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.basics.index.OvernightIndex;
import com.opengamma.collect.ArgChecker;

/**
 * Defines the calculation of a rate from a single Overnight index that is averaged.
 * <p>
 * An interest rate determined directly from an Overnight index by averaging the value
 * of each day's rate over the period.
 * For example, a rate determined from 'USD-FED-FUND'.
 */
@BeanDefinition
public final class OvernightAveragedRate
    implements Rate, ImmutableBean, Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * The Overnight index.
   * <p>
   * The rate to be paid is based on this index
   * It will be a well known market index such as 'USD-FED-FUND'.
   */
  @PropertyDefinition(validate = "notNull")
  private final OvernightIndex index;
  /**
   * The number of business days before the end of the period that the rate is cutoff.
   * <p>
   * When a rate cutoff applies, the final daily rate is determined this number of days
   * before the end of the period, with any subsequent days having the same rate.
   * <p>
   * The amount must be zero or positive.
   * A value of zero or one will have no effect on the standard calculation.
   * The fixing holiday calendar of the index is used to determine business days.
   * <p>
   * For example, a value of {@code -3} means that the rate observed on
   * {@code (periodEndDate - 3 business days)} is also to be used on
   * {@code (periodEndDate - 2 business days)} and {@code (periodEndDate - 1 business day)}.
   * <p>
   * If there are multiple accrual periods in the payment period, then this
   * should typically only be non-zero in the last accrual period.
   */
  @PropertyDefinition(validate = "ArgChecker.notNegative")
  private final int rateCutoffDaysOffset;

  //-------------------------------------------------------------------------
  /**
   * Creates an {@code OvernightAveragedRate} from an index with no rate cutoff.
   * 
   * @param index  the index
   * @return the Overnight compounded rate
   */
  public static OvernightAveragedRate of(OvernightIndex index) {
    return OvernightAveragedRate.builder()
        .index(index)
        .rateCutoffDaysOffset(0)
        .build();
  }

  /**
   * Creates an {@code OvernightAveragedRate} from an index and rate cutoff.
   * 
   * @param index  the index
   * @param rateCutoffDaysOffset  the rate cutoff days offset, zero or negative but not -1
   * @return the Overnight compounded rate
   */
  public static OvernightAveragedRate of(OvernightIndex index, int rateCutoffDaysOffset) {
    return OvernightAveragedRate.builder()
        .index(index)
        .rateCutoffDaysOffset(rateCutoffDaysOffset)
        .build();
  }

  //-------------------------------------------------------------------------
  @ImmutableValidator
  private void validate() {
    if (rateCutoffDaysOffset > 0 || rateCutoffDaysOffset == -1) {
      throw new IllegalArgumentException("Rate cutoff offset must be zero or negative, but not -1");
    }
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code OvernightAveragedRate}.
   * @return the meta-bean, not null
   */
  public static OvernightAveragedRate.Meta meta() {
    return OvernightAveragedRate.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(OvernightAveragedRate.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static OvernightAveragedRate.Builder builder() {
    return new OvernightAveragedRate.Builder();
  }

  private OvernightAveragedRate(
      OvernightIndex index,
      int rateCutoffDaysOffset) {
    JodaBeanUtils.notNull(index, "index");
    ArgChecker.notNegative(rateCutoffDaysOffset, "rateCutoffDaysOffset");
    this.index = index;
    this.rateCutoffDaysOffset = rateCutoffDaysOffset;
    validate();
  }

  @Override
  public OvernightAveragedRate.Meta metaBean() {
    return OvernightAveragedRate.Meta.INSTANCE;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the Overnight index.
   * <p>
   * The rate to be paid is based on this index
   * It will be a well known market index such as 'USD-FED-FUND'.
   * @return the value of the property, not null
   */
  public OvernightIndex getIndex() {
    return index;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the number of business days before the end of the period that the rate is cutoff.
   * <p>
   * When a rate cutoff applies, the final daily rate is determined this number of days
   * before the end of the period, with any subsequent days having the same rate.
   * <p>
   * The amount must be zero or positive.
   * A value of zero or one will have no effect on the standard calculation.
   * The fixing holiday calendar of the index is used to determine business days.
   * <p>
   * For example, a value of {@code -3} means that the rate observed on
   * {@code (periodEndDate - 3 business days)} is also to be used on
   * {@code (periodEndDate - 2 business days)} and {@code (periodEndDate - 1 business day)}.
   * <p>
   * If there are multiple accrual periods in the payment period, then this
   * should typically only be non-zero in the last accrual period.
   * @return the value of the property
   */
  public int getRateCutoffDaysOffset() {
    return rateCutoffDaysOffset;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      OvernightAveragedRate other = (OvernightAveragedRate) obj;
      return JodaBeanUtils.equal(getIndex(), other.getIndex()) &&
          (getRateCutoffDaysOffset() == other.getRateCutoffDaysOffset());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getIndex());
    hash += hash * 31 + JodaBeanUtils.hashCode(getRateCutoffDaysOffset());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("OvernightAveragedRate{");
    buf.append("index").append('=').append(getIndex()).append(',').append(' ');
    buf.append("rateCutoffDaysOffset").append('=').append(JodaBeanUtils.toString(getRateCutoffDaysOffset()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code OvernightAveragedRate}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code index} property.
     */
    private final MetaProperty<OvernightIndex> index = DirectMetaProperty.ofImmutable(
        this, "index", OvernightAveragedRate.class, OvernightIndex.class);
    /**
     * The meta-property for the {@code rateCutoffDaysOffset} property.
     */
    private final MetaProperty<Integer> rateCutoffDaysOffset = DirectMetaProperty.ofImmutable(
        this, "rateCutoffDaysOffset", OvernightAveragedRate.class, Integer.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "index",
        "rateCutoffDaysOffset");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 100346066:  // index
          return index;
        case -1252935529:  // rateCutoffDaysOffset
          return rateCutoffDaysOffset;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public OvernightAveragedRate.Builder builder() {
      return new OvernightAveragedRate.Builder();
    }

    @Override
    public Class<? extends OvernightAveragedRate> beanType() {
      return OvernightAveragedRate.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code index} property.
     * @return the meta-property, not null
     */
    public MetaProperty<OvernightIndex> index() {
      return index;
    }

    /**
     * The meta-property for the {@code rateCutoffDaysOffset} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Integer> rateCutoffDaysOffset() {
      return rateCutoffDaysOffset;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 100346066:  // index
          return ((OvernightAveragedRate) bean).getIndex();
        case -1252935529:  // rateCutoffDaysOffset
          return ((OvernightAveragedRate) bean).getRateCutoffDaysOffset();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code OvernightAveragedRate}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<OvernightAveragedRate> {

    private OvernightIndex index;
    private int rateCutoffDaysOffset;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(OvernightAveragedRate beanToCopy) {
      this.index = beanToCopy.getIndex();
      this.rateCutoffDaysOffset = beanToCopy.getRateCutoffDaysOffset();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 100346066:  // index
          return index;
        case -1252935529:  // rateCutoffDaysOffset
          return rateCutoffDaysOffset;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 100346066:  // index
          this.index = (OvernightIndex) newValue;
          break;
        case -1252935529:  // rateCutoffDaysOffset
          this.rateCutoffDaysOffset = (Integer) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder setString(String propertyName, String value) {
      setString(meta().metaProperty(propertyName), value);
      return this;
    }

    @Override
    public Builder setString(MetaProperty<?> property, String value) {
      super.setString(property, value);
      return this;
    }

    @Override
    public Builder setAll(Map<String, ? extends Object> propertyValueMap) {
      super.setAll(propertyValueMap);
      return this;
    }

    @Override
    public OvernightAveragedRate build() {
      return new OvernightAveragedRate(
          index,
          rateCutoffDaysOffset);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code index} property in the builder.
     * @param index  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder index(OvernightIndex index) {
      JodaBeanUtils.notNull(index, "index");
      this.index = index;
      return this;
    }

    /**
     * Sets the {@code rateCutoffDaysOffset} property in the builder.
     * @param rateCutoffDaysOffset  the new value
     * @return this, for chaining, not null
     */
    public Builder rateCutoffDaysOffset(int rateCutoffDaysOffset) {
      ArgChecker.notNegative(rateCutoffDaysOffset, "rateCutoffDaysOffset");
      this.rateCutoffDaysOffset = rateCutoffDaysOffset;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(96);
      buf.append("OvernightAveragedRate.Builder{");
      buf.append("index").append('=').append(JodaBeanUtils.toString(index)).append(',').append(' ');
      buf.append("rateCutoffDaysOffset").append('=').append(JodaBeanUtils.toString(rateCutoffDaysOffset));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
