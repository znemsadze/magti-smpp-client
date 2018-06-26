package ge.magticom.smpp.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;

public class ClassicFormatter extends java.util.logging.Formatter {

    private static SimpleDateFormat dateFormat = null;

    public ClassicFormatter() {
        if (dateFormat == null)
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
    }

    public String format(LogRecord record) {
        Date t = new Date(record.getMillis());
        String level = "[" + record.getLevel().toString() + "] ";
        String caller = " (" + record.getSourceClassName() + "." + record.getSourceMethodName() + ")";
        String time = dateFormat.format(t);
        DecimalFormat nf = new DecimalFormat();
        nf.setMinimumIntegerDigits(9);
        nf.setGroupingUsed(false);
        String id = "{" + nf.format(record.getSequenceNumber()) + "} ";
        String msg = record.getMessage();
        if (record.getThrown() != null) {
            msg += "\nException: " + record.getThrown().getClass().getName() + ": " + record.getThrown().getMessage() + "\n";
            StringWriter sw = new StringWriter();
            record.getThrown().printStackTrace(new PrintWriter(sw));
            msg += sw.toString();
        }
        String[] lines = msg.split("\n");
        if (lines.length == 1) {
            return id + time + level + msg + caller + "\n";
        } else {
            String res = id + time + level + caller + "\n";
            for (int i = 0; i < lines.length; i++)
                res += id + lines[i] + "\n";
            return res;
        }
    }
}
