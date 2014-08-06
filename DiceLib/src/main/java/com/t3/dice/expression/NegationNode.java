/*
 * Copyright (c) 2014 tabletoptool.com team.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     rptools.com team - initial implementation
 *     tabletoptool.com team - further development
 */
package com.t3.dice.expression;

import java.util.Random;

import com.t3.xstreamversioned.SerializationVersion;

@SerializationVersion(0)
public class NegationNode extends Expression {

	private Expression expr;

	public NegationNode(Expression expr) {
		this.expr=expr;
	}
	
	@Override
	public float getResult(Random random) {
		return -expr.getResult(random);
	}
	

	@Override
	public String toString() {
		return "-"+expr.toString();
	}

	@Override
	public String toEvaluatedString() {
		return "-("+expr.toEvaluatedString()+")";
	}

}
