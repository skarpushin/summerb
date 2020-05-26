package org.summerb.dbupgrade.api;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * This strategy knows how to parse input stream into sequence of executable
 * statements
 * 
 * @author sergeyk
 *
 */
public interface SqlPackageParser {

	Stream<UpgradeStatement> getUpgradeScriptsStream(InputStream is) throws Exception;

}
