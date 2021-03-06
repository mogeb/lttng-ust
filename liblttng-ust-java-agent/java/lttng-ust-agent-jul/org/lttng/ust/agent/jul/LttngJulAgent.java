/*
 * Copyright (C) 2015 - EfficiOS Inc., Alexandre Montplaisir <alexmonthy@efficios.com>
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License, version 2.1 only,
 * as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.lttng.ust.agent.jul;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.lttng.ust.agent.AbstractLttngAgent;

/**
 * Agent implementation for tracing from JUL loggers.
 *
 * @author Alexandre Montplaisir
 */
class LttngJulAgent extends AbstractLttngAgent<LttngLogHandler> {

	private static LttngJulAgent instance = null;

	private LttngJulAgent() {
		super(Domain.JUL);
	}

	public static synchronized LttngJulAgent getInstance() {
		if (instance == null) {
			instance = new LttngJulAgent();
		}
		return instance;
	}

	@Override
	public Collection<String> listAvailableEvents() {
		List<String> ret = new ArrayList<String>();

		List<String> loggersNames = Collections.list(LogManager.getLogManager().getLoggerNames());
		for (String name : loggersNames) {
			/*
			 * Skip the root logger. An empty string is not a valid event name
			 * in LTTng.
			 */
			if (name.equals("")) {
				continue;
			}

			/*
			 * Check if that logger has at least one LTTng JUL handler attached.
			 */
			Logger logger = Logger.getLogger(name);
			if (hasLttngHandlerAttached(logger)) {
				ret.add(name);
			}
		}

		return ret;
	}

	private static boolean hasLttngHandlerAttached(Logger logger) {
		for (Handler handler : logger.getHandlers()) {
			if (handler instanceof LttngLogHandler) {
				return true;
			}
		}
		return false;
	}

}
