package com.t3.macro.api.tokenfilter;

import com.t3.model.Token;
import com.t3.model.Zone;

/**
 * Filter for PC tokens.
 */
public class PCFilter implements TokenFilter {
	public boolean matchToken(Token t) {
		return t.getType() == Token.Type.PC;
	}
}
