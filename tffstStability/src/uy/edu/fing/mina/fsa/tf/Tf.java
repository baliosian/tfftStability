/* 
 * es.upc.tffst
 * Copyright (C) 2004 Javier Baliosian
 * All rights reserved.
 *
 */

package uy.edu.fing.mina.fsa.tf;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import orbital.logic.imp.Formula;

import uy.edu.fing.mina.fsa.logics.Utils;

/**
 * Simple Condition.
 * 
 * @author Javier Baliosian &lt; <a
 *         href="mailto:jbaliosian@tsc.upc.es">jbaliosian@tsc.upc.es </a>&gt;
 */

public abstract class Tf implements TfI, Cloneable, Comparable {

//  private boolean atomic = true;

  protected boolean not = false;

  private int identity = 0;

  private TfI identityTf = null;
  
  private static int sequence=1;
  
  private int id;
  
  private TfI refersTo = null;
  
  /**
   * if is not null, this set a fixed value for the TF
   */
  private float value = Float.NaN;
  
  protected Formula formula;

  public Tf() {
    this(false, false, "", Float.NaN);
  }

  /**
   * @param atomic
   * @param event
   * @param eventName
   * @param isEpsilon
   * @param not
   * @param label
   * @param value
   */
  public Tf(boolean atomic, boolean not, String label, float value) {
    super();
    this.not = not;
    this.value = value;
    
    this.id=sequence++;
  }

  public Tf(float value) {
    this(false, false, "", value);
  }

  /**
   * it sets as not a set of tfs it must be removed when tffsr.determinize would
   * be fixed
   * 
   * @param tfs
   * @return
   */
  public static Set<Tf> notS(Set<Tf> tfs) {
    Set<Tf> newtfs = new HashSet<Tf>();
    for (Iterator<Tf> iter = tfs.iterator(); iter.hasNext();) {
      Tf tf = iter.next();
      tf.setNot(!tf.isNot());
    }
    return newtfs;
  }

  abstract public boolean acceptsAll();

  abstract public boolean acceptsNone();

  public TfI or(TfI tf) {
	    TfI outTf;

	    // --
	    if (this.acceptsAll() || tf.acceptsAll()) {
	        SimpleTf stf = new SimpleTf();
	        stf.setAcceptAll();
	        outTf = stf;
	      } else if (this.acceptsNone()) {
	        outTf = tf;
	      } else if (tf.acceptsNone()) {
	        outTf = this;
	      } else if (this.equals(tf)) {
	        outTf = this;
	      } else if (this.equals(tf.not())) {
	        SimpleTf stf = new SimpleTf();
	        stf.setAcceptAll();
	        outTf = stf;
	      }else if (this instanceof SimpleTf && tf instanceof SimpleTf ) {
	        outTf = new CompositeTf(Operator.OR, this, tf);
	      } else {
	        outTf = new CompositeTf(Operator.OR, this, tf);
	        outTf = Utils.simplify(outTf);
	      }
	    // --
	    return outTf;
	  }

  
  public TfI and(TfI tf) {
    TfI outTf;
    // --
    if (this.acceptsNone() || tf.acceptsNone()) {
      SimpleTf stf = new SimpleTf();
      stf.setAcceptNone();
      outTf = stf;
    } else if (this.acceptsAll()) {
      outTf = tf;
    } else if (tf.acceptsAll()) {
      outTf = this;
    } else if (this.equals(tf)) {
      outTf = this;
    } else if (this.equals(tf.not())) {
      SimpleTf stf = new SimpleTf();
      stf.setAcceptNone();
      outTf = stf;
    }else if (this instanceof SimpleTf && tf instanceof SimpleTf ) {
      outTf = new CompositeTf(Operator.AND, this, tf);
    } else {
      outTf = new CompositeTf(Operator.AND, this, tf);
      outTf = Utils.simplify(outTf);
    }
    // --
    return outTf;
  }

  public TfI asTautas(TfI tf) {
    TfI outTf;

    // --
    if (this.acceptsNone() || tf.acceptsNone()) {
      SimpleTf stf = new SimpleTf();
      stf.setAcceptNone();
      outTf = stf;
    } else if (this.equals(tf)) {
      outTf = this;
    } else if (this.equals(tf.not())) {
      SimpleTf stf = new SimpleTf();
      stf.setAcceptNone();
      outTf = stf;
    } else {
      outTf = new CompositeTf(Operator.AS_TAUT_AS, this, tf);
    }
    // --

    outTf = new CompositeTf(Operator.AS_TAUT_AS, this, tf);
    return outTf;
  }

  /*
   * @see java.lang.Object#clone()
   */
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }


  /*
   * (non-Javadoc)
   * 
   * @see uy.edu.fing.mina.omega.tffst.utils.tf.TfI#getFixedValue()
   */
  public float getFixedValue() {
    return value;
  }

  /**
   * @return Returns the slabel.
   */
  abstract public String getSLabel();

  /**
   * @return Returns the value.
   */
  public float getValue() {
    return value;
  }

  /**
   * @return Returns the isEpsilon.
   */
  abstract public boolean isEpsilon();

  /*
   * @see uy.edu.fing.mina.omega.tffst.utils.tf.TfI#isNot()
   */
  public boolean isNot() {
    return not;
  }

  public TfI not() {
    TfI r;
    try {
      r = (TfI) clone();
      
      if (acceptsAll())
        ((SimpleTf) r).setAcceptNone();
      else if (acceptsNone())
        ((SimpleTf) r).setAcceptAll();
      else {
        r.setNot(!isNot());
        r.setFormula(getFormula().not());
      }
      
      return r;
      
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return null;
  }



  /**
   * @param value
   *          The value to set.
   */
  public void setValue(float value) {
    this.value = value;
  }


  public TfI tauterThan(TfI tf) {
    TfI outTf;
    // --
    if (this.acceptsNone()) {
      SimpleTf stf = new SimpleTf();
      stf.setAcceptNone();
      outTf = stf;
    } else if (this.acceptsAll()) {
      SimpleTf stf = new SimpleTf();
      stf.setAcceptNone();
      outTf = stf;
    } else if (tf.acceptsAll()) {
      outTf = this;
    } else if (this.equals(tf)) {
      SimpleTf stf = new SimpleTf();
      stf.setAcceptNone();
      outTf = stf;
    } else if (this.equals(tf.not())) {
      outTf = this; // TODO check this
    } else {
      outTf = new CompositeTf(Operator.TAUTER_THAN, this, tf);
    }
    // --
    return outTf;
  }

  public abstract String toString();



  public int getIdentity() {
    return this.identity;
  }

  public void setIdentity(int identity) {
    this.identity = identity;
  }

  public void setIdentityTf(TfI labelIn) {
    this.identityTf = labelIn;
  }

  public TfI getIdentityTf() {
    return identityTf;
  }

public int getId() {
	return id;
}

public TfI getRefersTo() {
	return refersTo;
}

public void setRefersTo(TfI refersTo) {
	this.refersTo = refersTo;
}

/**
 * beware, you should not use this
 * 
 */
public void setFormula(Formula formula) {
  this.formula = formula;
}

public void setNot(boolean not) {
	this.not = not;
	this.formula = getFormula().not();
}

//  abstract public void setCondition(ConditionI condition);

}