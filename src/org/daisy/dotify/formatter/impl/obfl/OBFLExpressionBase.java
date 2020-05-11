package org.daisy.dotify.formatter.impl.obfl;

import org.daisy.dotify.api.formatter.Context;
import org.daisy.dotify.api.obfl.ExpressionFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * TODO: Write java doc.
 * TODO: Remove the defaults. See https://github.com/mtmse/obfl/issues/13
 */
public abstract class OBFLExpressionBase {

    public static final String DEFAULT_PAGE_NUMBER_VARIABLE_NAME = "page";
    public static final String DEFAULT_VOLUME_NUMBER_VARIABLE_NAME = "volume";
    public static final String DEFAULT_VOLUME_COUNT_VARIABLE_NAME = "volumes";
    public static final String DEFAULT_STARTED_VOLUME_NUMBER_VARIABLE_NAME = "started-volume-number";
    public static final String DEFAULT_STARTED_PAGE_NUMBER_VARIABLE_NAME = "started-page-number";
    public static final String DEFAULT_STARTED_VOLUME_FIRST_CONTENT_PAGE_NUMBER_VARIABLE_NAME
        = "started-volume-first-content-page";
    public static final String DEFAULT_SHEET_COUNT_VARIABLE_NAME = "sheets-in-document";
    public static final String DEFAULT_VOLUME_SHEET_COUNT_VARIABLE_NAME = "sheets-in-volume";

    protected final ExpressionFactory ef;
    protected final String exp;

    protected String pageNumberVariableName = null;
    protected String volumeNumberVariableName = null;
    protected String volumeCountVariableName = null;
    protected String metaVolumeNumberVariableName = null;
    protected String metaPageNumberVariableName = null;
    protected String sheetCountVariableName = null;
    protected String volumeSheetCountVariableName = null;

    private final Set<OBFLVariable> variables;
    /**
     * @param exp The expression string
     * @param ef The expression factory
     * @param variables The variables (zero or more) that may be used within the expression.
     *            <p>Default names are given to the variables. To provide different names use the
     *            {@link #setVariableName(OBFLVariable, String)} method. Note that whether a
     *            variable will actually be assigned a value is not determined by this object. This
     *            depends on the context in which the expression is used.</p> <p>The variables
     *            {@link OBFLVariable#STARTED_PAGE_NUMBER} and {@link
     *            OBFLVariable#STARTED_VOLUME_FIRST_CONTENT_PAGE_NUMBER} can not both be used in the
     *            same expression. At most one "<i>meta</i>" page number is available at any
     *            particular place in the OBFL. Which of the two is included in the
     *            <code>variables</code> argument does not affect the value that will be assigned,
     *            only the variable name. The value is determined by the context.</p>
     */
    public OBFLExpressionBase(String exp, ExpressionFactory ef, OBFLVariable... variables) {
        this.ef = ef;
        this.exp = exp;
        this.variables = new HashSet<>();
        for (OBFLVariable v : variables) {
            switch (v) {
            case PAGE_NUMBER:
            case VOLUME_NUMBER:
            case VOLUME_COUNT:
            case STARTED_VOLUME_NUMBER:
                break;
            case STARTED_PAGE_NUMBER:
                if (this.variables.contains(OBFLVariable.STARTED_VOLUME_FIRST_CONTENT_PAGE_NUMBER)) {
                    throw new IllegalArgumentException(
                        "STARTED_PAGE_NUMBER and STARTED_VOLUME_FIRST_CONTENT_PAGE_NUMBER " +
                        "may not both be used in the same expression.");
                }
                break;
            case STARTED_VOLUME_FIRST_CONTENT_PAGE_NUMBER:
                if (this.variables.contains(OBFLVariable.STARTED_PAGE_NUMBER)) {
                    throw new IllegalArgumentException(
                        "STARTED_PAGE_NUMBER and STARTED_VOLUME_FIRST_CONTENT_PAGE_NUMBER " +
                        "may not both be used in the same expression.");
                }
                break;
            case SHEET_COUNT:
            case VOLUME_SHEET_COUNT:
                break;
            default:
                throw new IllegalArgumentException(); // coding error
            }
            this.variables.add(v);
            setVariableName(v, null);
        }
    }

    public void setVariableName(OBFLVariable variable, String name) {
        if (!variables.contains(variable)) {
            throw new IllegalArgumentException("variable " + variable + " not made available in this expression");
        }
        switch (variable) {
        case PAGE_NUMBER:
            this.pageNumberVariableName = name != null ? name : DEFAULT_PAGE_NUMBER_VARIABLE_NAME;
            break;
        case VOLUME_NUMBER:
            this.volumeNumberVariableName = name != null ? name : DEFAULT_VOLUME_NUMBER_VARIABLE_NAME;
            break;
        case VOLUME_COUNT:
            this.volumeCountVariableName = name != null ? name : DEFAULT_VOLUME_COUNT_VARIABLE_NAME;
            break;
        case STARTED_VOLUME_NUMBER:
            this.metaVolumeNumberVariableName = name != null ? name : DEFAULT_STARTED_VOLUME_NUMBER_VARIABLE_NAME;
            break;
        case STARTED_PAGE_NUMBER:
            this.metaPageNumberVariableName = name != null ? name : DEFAULT_STARTED_PAGE_NUMBER_VARIABLE_NAME;
            break;
        case STARTED_VOLUME_FIRST_CONTENT_PAGE_NUMBER:
            this.metaPageNumberVariableName = name != null
                ? name
                : DEFAULT_STARTED_VOLUME_FIRST_CONTENT_PAGE_NUMBER_VARIABLE_NAME;
            break;
        case SHEET_COUNT:
            this.sheetCountVariableName = name != null ? name : DEFAULT_SHEET_COUNT_VARIABLE_NAME;
            break;
        case VOLUME_SHEET_COUNT:
            this.volumeSheetCountVariableName = name != null ? name : DEFAULT_VOLUME_SHEET_COUNT_VARIABLE_NAME;
            break;
        default:
            throw new IllegalArgumentException(); // coding error
        }
    }

    protected Map<String, String> buildArgs(Context context) {
        HashMap<String, String> variables = new HashMap<>();
        if (pageNumberVariableName != null) {
            variables.put(pageNumberVariableName, "" + context.getCurrentPage());
        }
        if (volumeNumberVariableName != null) {
            // Passing a default value for the case the current volume is not known. This is the
            // case during the preparation phase of the VolumeProvider. If we wouldn't pass a value,
            // the evaluation of an expression with "$volume" would fail. Passing the value "??"
            // would not work because $volume is expected to be a number, so e.g. arithmetic
            // operations can be applied to it.
            variables.put(
                volumeNumberVariableName,
                context.getCurrentVolume() == null ? "0" : ("" + context.getCurrentVolume())
            );
        }
        if (volumeCountVariableName != null) {
            variables.put(volumeCountVariableName, "" + context.getVolumeCount());
        }
        // The meta variables below are only available in a meta-context. If
        // they are used incorrectly in a context where the meta-context is
        // unavailable, then they evaluate to null, which may result for
        // instance in the literal text "null" to appear in the content, without
        // warning or error.
        // TODO: Fix this issue.
        if (metaVolumeNumberVariableName != null) {
            variables.put(metaVolumeNumberVariableName, "" + context.getMetaVolume());
        }
        if (metaPageNumberVariableName != null) {
            variables.put(metaPageNumberVariableName, "" + context.getMetaPage());
        }
        if (sheetCountVariableName != null) {
            variables.put(sheetCountVariableName, "" + context.getSheetsInDocument());
        }
        if (volumeSheetCountVariableName != null) {
            variables.put(volumeSheetCountVariableName, "" + context.getSheetsInVolume());
        }
        return variables;
    }

}
