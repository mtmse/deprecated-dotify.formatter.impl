package org.daisy.dotify.formatter.impl.obfl;

import org.daisy.dotify.api.formatter.Context;
import org.daisy.dotify.api.obfl.ExpressionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Write java doc.
 * TODO: Remove the defaults. See https://github.com/mtmse/obfl/issues/13
 */
public abstract class OBFLExpressionBase {
    public static final String DEFAULT_PAGE_NUMBER_VARIABLE_NAME = "page";
    public static final String DEFAULT_VOLUME_NUMBER_VARIABLE_NAME = "volume";
    public static final String DEFAULT_VOLUME_COUNT_VARIABLE_NAME = "volumes";
    public static final String DEFAULT_EVENT_VOLUME_NUMBER = "started-volume-number";
    public static final String DEFAULT_EVENT_PAGE_NUMBER = "started-page-number";
    public static final String DEFAULT_SHEET_COUNT_VARIABLE_NAME = "sheets-in-document";
    public static final String DEFAULT_VOLUME_SHEET_COUNT_VARIABLE_NAME = "sheets-in-volume";
    public static final String STARTED_VOLUME_FIRST_CONTENT_PAGE_NUMBER = "started-volume-first-content-page";

    protected final ExpressionFactory ef;
    protected final String exp;

    protected String pageNumberVariable;
    protected String volumeNumberVariable;
    protected String volumeCountVariable;
    protected String metaVolumeNumberVariable;
    protected String metaPageNumberVariable;
    protected String sheetCountVariable;
    protected String volumeSheetCountVariable;

    public OBFLExpressionBase(String exp, ExpressionFactory ef, MetaVariable... metaVariables) {
        this.ef = ef;
        this.exp = exp;
        this.pageNumberVariable = DEFAULT_PAGE_NUMBER_VARIABLE_NAME;
        this.volumeNumberVariable = DEFAULT_VOLUME_NUMBER_VARIABLE_NAME;
        this.volumeCountVariable = DEFAULT_VOLUME_COUNT_VARIABLE_NAME;
        this.sheetCountVariable = DEFAULT_SHEET_COUNT_VARIABLE_NAME;
        this.volumeSheetCountVariable = DEFAULT_VOLUME_SHEET_COUNT_VARIABLE_NAME;
        
        this.metaVolumeNumberVariable = null;
        this.metaPageNumberVariable = null;
        for (MetaVariable metaVariable : metaVariables) {
            switch (metaVariable) {
                case STARTED_VOLUME_NUMBER:
                    this.metaVolumeNumberVariable = DEFAULT_EVENT_VOLUME_NUMBER;
                    break;
                case STARTED_PAGE_NUMBER:
                    this.metaPageNumberVariable = DEFAULT_EVENT_PAGE_NUMBER;
                    break;
                case STARTED_VOLUME_FIRST_CONTENT_PAGE_NUMBER:
                    this.metaPageNumberVariable = STARTED_VOLUME_FIRST_CONTENT_PAGE_NUMBER;
                    break;
                default:
            }
        }
    }

    public void setPageNumberVariable(String pageNumberVariable) {
        if (pageNumberVariable == null) {
            this.pageNumberVariable = DEFAULT_PAGE_NUMBER_VARIABLE_NAME;
        } else {
            this.pageNumberVariable = pageNumberVariable;
        }
    }

    public void setVolumeNumberVariable(String volumeNumberVariable) {
        if (volumeNumberVariable == null) {
            this.volumeNumberVariable = DEFAULT_VOLUME_NUMBER_VARIABLE_NAME;
        } else {
            this.volumeNumberVariable = volumeNumberVariable;
        }
    }

    public void setVolumeCountVariable(String volumeCountVariable) {
        if (volumeCountVariable == null) {
            this.volumeCountVariable = DEFAULT_VOLUME_COUNT_VARIABLE_NAME;
        } else {
            this.volumeCountVariable = volumeCountVariable;
        }
    }

    public void setMetaVolumeNumberVariable(String metaVolumeNumberVariable) {
        if (metaVolumeNumberVariable == null) {
            this.metaVolumeNumberVariable = DEFAULT_EVENT_VOLUME_NUMBER;
        } else {
            this.metaVolumeNumberVariable = metaVolumeNumberVariable;
        }
    }

    public void setMetaPageNumberVariable(String metaPageNumberVariable) {
        if (metaPageNumberVariable == null) {
            this.metaPageNumberVariable = DEFAULT_EVENT_PAGE_NUMBER;
        } else {
            this.metaPageNumberVariable = metaPageNumberVariable;
        }
    }

    protected Map<String, String> buildArgs(Context context) {
        HashMap<String, String> variables = new HashMap<>();
        if (pageNumberVariable != null) {
            variables.put(pageNumberVariable, "" + context.getCurrentPage());
        }
        if (volumeNumberVariable != null) {
            // Passing a default value for the case the current volume is not known. This is the
            // case during the preparation phase of the VolumeProvider. If we wouldn't pass a value,
            // the evaluation of an expression with "$volume" would fail. Passing the value "??"
            // would not work because $volume is expected to be a number, so e.g. arithmetic
            // operations can be applied to it.
            variables.put(
                volumeNumberVariable,
                context.getCurrentVolume() == null ? "0" : ("" + context.getCurrentVolume())
            );
        }
        if (volumeCountVariable != null) {
            variables.put(volumeCountVariable, "" + context.getVolumeCount());
        }
        // The meta variables below are only available in a meta-context. If
        // they are used incorrectly in a context where the meta-context is
        // unavailable, then they evaluate to null, which may result for
        // instance in the literal text "null" to appear in the content, without
        // warning or error.
        // TODO: Fix this issue.
        if (metaVolumeNumberVariable != null) {
            variables.put(metaVolumeNumberVariable, "" + context.getMetaVolume());
        }
        if (metaPageNumberVariable != null) {
            variables.put(metaPageNumberVariable, "" + context.getMetaPage());
        }
        if (sheetCountVariable != null) {
            variables.put(sheetCountVariable, "" + context.getSheetsInDocument());
        }
        if (volumeSheetCountVariable != null) {
            variables.put(volumeSheetCountVariable, "" + context.getSheetsInVolume());
        }
        return variables;
    }

}
