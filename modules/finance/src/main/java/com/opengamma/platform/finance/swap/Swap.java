/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.platform.finance.swap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.google.common.collect.ImmutableList;

/**
 * The details of an interest rate swap, separate from a trade.
 * <p>
 * An swap takes place between two counterparties who agree to exchange streams of payments.
 * In the simplest vanilla swap, there are two legs, one with a fixed rate and the other a floating rate.
 * <p>
 * An instance of {@code Swap} can exist separate from a {@link SwapTrade}.
 */
@BeanDefinition
public final class Swap
    implements ImmutableBean, Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * The legs of the swap.
   */
  @PropertyDefinition(validate = "notEmpty")
  private final ImmutableList<SwapLeg> legs;

  //-------------------------------------------------------------------------
  /**
   * Creates a swap from one or more swap legs.
   * <p>
   * While most swaps have two legs, other combinations are possible.
   * 
   * @param legs  the array of legs
   * @return the swap
   */
  public static Swap of(SwapLeg... legs) {
    return Swap.builder()
        .legs(ImmutableList.copyOf(legs))
        .build();
  }

  //-------------------------------------------------------------------------
  /**
   * Gets a swap leg by index.
   * <p>
   * This returns a leg using a zero-based index.
   * 
   * @param index  the zero-based period index
   * @return the swap leg
   * @throws IndexOutOfBoundsException if the index is invalid
   */
  public SwapLeg getLeg(int index) {
    return legs.get(index);
  }

  //-------------------------------------------------------------------------
  /**
   * Checks if this trade is cross-currency.
   * <p>
   * A cross currency swap is defined as one with legs in two different currencies.
   * 
   * @return true if cross currency
   */
  public boolean isCrossCurrency() {
    return legs.stream()
        .map(SwapLeg::getCurrency)
        .distinct()
        .count() > 1;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code Swap}.
   * @return the meta-bean, not null
   */
  public static Swap.Meta meta() {
    return Swap.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(Swap.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static Swap.Builder builder() {
    return new Swap.Builder();
  }

  private Swap(
      List<SwapLeg> legs) {
    JodaBeanUtils.notEmpty(legs, "legs");
    this.legs = ImmutableList.copyOf(legs);
  }

  @Override
  public Swap.Meta metaBean() {
    return Swap.Meta.INSTANCE;
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
   * Gets the legs of the swap.
   * @return the value of the property, not empty
   */
  public ImmutableList<SwapLeg> getLegs() {
    return legs;
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
      Swap other = (Swap) obj;
      return JodaBeanUtils.equal(getLegs(), other.getLegs());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getLegs());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(64);
    buf.append("Swap{");
    buf.append("legs").append('=').append(JodaBeanUtils.toString(getLegs()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code Swap}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code legs} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableList<SwapLeg>> legs = DirectMetaProperty.ofImmutable(
        this, "legs", Swap.class, (Class) ImmutableList.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "legs");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3317797:  // legs
          return legs;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public Swap.Builder builder() {
      return new Swap.Builder();
    }

    @Override
    public Class<? extends Swap> beanType() {
      return Swap.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code legs} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableList<SwapLeg>> legs() {
      return legs;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 3317797:  // legs
          return ((Swap) bean).getLegs();
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
   * The bean-builder for {@code Swap}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<Swap> {

    private List<SwapLeg> legs = new ArrayList<SwapLeg>();

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(Swap beanToCopy) {
      this.legs = new ArrayList<SwapLeg>(beanToCopy.getLegs());
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3317797:  // legs
          return legs;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 3317797:  // legs
          this.legs = (List<SwapLeg>) newValue;
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
    public Swap build() {
      return new Swap(
          legs);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code legs} property in the builder.
     * @param legs  the new value, not empty
     * @return this, for chaining, not null
     */
    public Builder legs(List<SwapLeg> legs) {
      JodaBeanUtils.notEmpty(legs, "legs");
      this.legs = legs;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(64);
      buf.append("Swap.Builder{");
      buf.append("legs").append('=').append(JodaBeanUtils.toString(legs));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
