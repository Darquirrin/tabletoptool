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
package com.t3.guid;

public class InvalidGUIDException extends RuntimeException {
    private static final long serialVersionUID = 3257568421032768820L;

    public InvalidGUIDException() {
        super();
    }

    public InvalidGUIDException(String s) {
        super(s);
    }

}