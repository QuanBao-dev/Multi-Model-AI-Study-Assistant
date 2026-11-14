package strategy;

public final class JsonText {

    /** Marker added when the provided client cut the reply off at an escaped quotation mark. */
    public static final String TRUNCATED_NOTE = " [response truncated]";

    private JsonText() {
    }

    /** Makes {@code s} safe to place between the quotes of a JSON string. */
    static String escape(String s) {
        StringBuilder out = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"':  out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default:
                    if (c < 0x20) out.append(String.format("\\u%04x", (int) c));
                    else out.append(c);
            }
        }
        return out.toString();
    }

    /**
     * Reverses JSON escaping in text taken from between JSON quotes (\n, \", \\, \\uXXXX ...).
     * If the text ends in a lone backslash, the provided client cut the reply off at an
     * escaped quote: the backslash is dropped and TRUNCATED_NOTE is appended.
     */
    static String unescape(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder out = new StringBuilder();
        boolean truncated = false;
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i++);
            if (c != '\\') {
                out.append(c);
                continue;
            }
            if (i >= s.length()) {          // lone trailing backslash
                truncated = true;
                break;
            }
            char e = s.charAt(i++);
            switch (e) {
                case 'n': out.append('\n'); break;
                case 't': out.append('\t'); break;
                case 'r': out.append('\r'); break;
                case 'b': out.append('\b'); break;
                case 'f': out.append('\f'); break;
                case 'u':
                    if (i + 4 <= s.length()) {
                        try {
                            out.append((char) Integer.parseInt(s.substring(i, i + 4), 16));
                            i += 4;
                            break;
                        } catch (NumberFormatException ignored) {
                            // fall through: keep the text as it was
                        }
                    }
                    out.append("\\u");
                    break;
                default: out.append(e);      // \" \\ \/
            }
        }
        if (truncated) {
            while (out.length() > 0 && Character.isWhitespace(out.charAt(out.length() - 1))) {
                out.setLength(out.length() - 1);
            }
            out.append(TRUNCATED_NOTE);
        }
        return out.toString();
    }
}
