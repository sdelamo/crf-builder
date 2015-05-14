package org.modelcatalogue.crf.model;

import org.modelcatalogue.crf.model.validation.ValidationConstants;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import java.util.*;

import static org.modelcatalogue.crf.model.validation.ValidationConstants.ALPHA_NUMERIC_ENGLISH_NO_SPACES_PATTERN;
import static org.modelcatalogue.crf.model.validation.ValidationConstants.WIDTH_DECIMAL_PATTERN;

public class Item {

    // TODO: validate default value supported
    // TODO: validate width decimal
    // TODO: validate validation error message is present when validation is set


    Item() {}

    static String storeResponseOptions(Iterable<String> options) {
        List<String> encoded = new ArrayList<String>();
        for (String option : options) {
            encoded.add(option.replaceAll(",", "/,"));
        }
        return join(encoded, ",");
    }

    static List<String> parseResponseOptions(String options) {
        List<String> parsed = new ArrayList<String>();
        for (String option : options.split("\\s*(?<!/),\\s*")) {
            parsed.add(option.replaceAll("/,", ","));
        }
        return Collections.unmodifiableList(parsed);
    }

    private static String join(List<String> list, String delim) {

        StringBuilder sb = new StringBuilder();

        String loopDelim = "";

        for(String s : list) {

            sb.append(loopDelim);
            sb.append(s);

            loopDelim = delim;
        }

        return sb.toString();
    }

    /**
     * The unique label or variable name for the data element.
     * The field is not displayed as part of the CRF but can be viewed as part of the CRF and Item Metadata, and is shown
     * in the Discrepancy Notes and Rules modules.
     *
     * This field is case-sensitive. Items with names “item1” and “Item1” will be treated as different items. This can
     * cause issues with many data analysis tools and should be avoided in most cases.
     *
     * For Enterprise customers interested in using Datamart:
     * Please note that Datamart treat items in case-insensitive manner. Please treat all ITEM_NAMES as case-insensitive.
     * Also for use with Datamart, Postgres has a list of reserved words and special characters which should not be used
     * as item names.
     */
    @NotNull @Size(min = 1, max = 255) @Pattern(regexp = ALPHA_NUMERIC_ENGLISH_NO_SPACES_PATTERN) private String name;

    /**
     * The description or definition of the item. Should give an explanation of the data element and the value(s) it
     * captures. It is not shown on the CRF but is in the data dictionary.
     */
    @NotNull @Size(min = 1, max = 4000) private String descriptionLabel;

    /**
     * Descriptive text that appears to the left of the input on the CRF. Often phrased in the form of a question, or
     * descriptive label for the form field input.
     */
    @Size(max = 2000) private String leftItemText;

    /**
     * Used to define the type of values being collected.  It appears to the right of the input field on the CRF.
     */
    @Size(max = 64) private String units;

    /**
     * Descriptive text that appears to the right of the form input on the CRF, and to the right of any UNITS that are
     * specified too. Often phrased in the form of a question, or supporting instructions for the form field input.
     */
    @Size(max = 2000) private String rightItemText;

    /**
     * Logically organizes the items that should be together on a section.
     *
     * The items in a given section are displayed on a single web page when the user is entering data, and appear
     * in the order they are entered in the Template.
     *
     * Every item in the worksheet must be assigned to a section of the CRF.
     */
    @NotNull @Valid private Section section;

    /**
     * Assigns items to an item group.  If the group is repeating, the items need to have the same SECTION_LABEL as
     * all other items in the group and must be consecutively defined in the ITEMS worksheet.
     *
     * Repeating items are displayed on a single row with the LEFT_ITEM_TEXT (if any exists) as a column header.
     */
    @Valid private Group group;

    /**
     * Contains text that used as a header for a particular item. Using this field will break up the items with
     * a distinct line between the header information and the next set of items. The text is bolded to call greater
     * attention to it.
     */
    @Size(max = 2000) private String header;


    /**
     * This field can contain text that will be used underneath the HEADER, or independently of a HEADER being provided.
     * The text will be separated by a line and have a grey background.
     */
    @Size(max = 240) private String subheader;

    /**
     * This field can contain an ITEM_NAME that immediately precedes this item, and is in the same section.
     *
     * This will cause the child item to be indented underneath the ITEM_NAME specified
     */
    @Valid private Item parent;

    /**
     * Assigns items to an item group.  If the group is repeating, the items need to have the same SECTION_LABEL as all
     * other items in the group and must be consecutively defined in the ITEMS worksheet.  Repeating items are displayed
     * on a single row with the LEFT_ITEM_TEXT (if any exists) as a column header.
     */
    @Min(1) private Integer columnNumber;

    /**
     * The page number on which the section begins. If using paper source documents and have a multi-page CRF,
     * put in the printed page number.
     */
    @Size(max = 5) @Pattern(regexp = ValidationConstants.ALPHA_NUMERIC_PATTERN) private String pageNumber;

    /**
     * This field is used to specify an identifier for each item or question in the Items worksheet.  It appears to the
     * left of the LEFT_ITEM_TEXT field, or if that field was left blank, to the left of the form input.
     */
    @Size(max = 20) @Pattern(regexp = ValidationConstants.ALPHA_NUMERIC_PATTERN) private String questionNumber;

    /**
     * The types of responses are based on standard HTML elements web browsers can display in a form. Allowed use of
     * the available RESPONSE_TYPEs depends on the item DATA_TYPE and use of Response Sets.
     */
    @NotNull private ResponseType responseType;

    /**
     * Create a custom label associated with a response set. This label must be defined once and may be reused by other i
     * tems with the same responses (e.g. Yes, No) and values.
     */
    @Size(max = 80) @Pattern(regexp = ValidationConstants.ALPHA_NUMERIC_PATTERN) private String responseLabel;

    /**
     * A comma delimited string of values that will be used as the options to be chosen by a data entry person when
     * they are entering data in a CRF.
     *
     * This field is only used for checkbox, multi-select, radio and single-select fields.  This will be the text
     * displayed to the data entry person, which they will choose for each item.  If the options themselves contain
     * commas (,) you must escape the commas with a /
     */
    @Size(max = 4000) private String responseOptionsText;

    /**
     * If the field is not a calculation or group-calculation, this will be a comma-delimited string of values that will
     * be used as the values saved to the database when a user chooses the corresponding RESPONSE_OPTIONS_TEXT.
     *
     * If this is a calculation or group-calculation field, it will be an expression that takes the inputs of other
     * items in the Items worksheet that are of INT or REAL data type to calculate a value.
     *
     * For checkbox, multi-select, radio and single-select fields, this will be the values that correspond to a
     * RESPONSE_OPTIONS_TEXT.  The number of options and values must match exactly or the CRF will be rejected when it
     * is uploaded into OpenClinica.
     *
     * The following calculations are allowed in this field if the RESPONSE_TYPE is calculation, sum(), avg(), min(),
     * max(), median(), stdev(), pow(), and decode().
     *
     * Cumulative calculations on a group of repeating items must be of type group-calculation. Only cumulative
     * calculations on the entire set of repeating items are allowed. The allowed functions are sum(), avg(), min(),
     * max(), median(), and stdev().
     * For example, in an invoice with a repeating group of line items, the calculation for a total price would be
     * the group-calculation “func: (sum (LINE_ITEM_PRICE))”.
     *
     * Instant calculation fields should use this field to define the onchange() function with arguments of an item name
     * (the trigger item) and value.
     */
    @Size(max = 4000) private String responseValuesOrCalculations;

    /**
     * The layout of the options for radio and checkbox fields.
     *
     * The options can be left to right, or top to bottom depending on the value specified in the Items worksheet.
     *
     * Leaving the field blank and selecting Vertical display the items in a single column from top to bottom.
     * Choosing Horizontal will put the items in a single row, left to right.
     */
    private ResponseLayout responseLayout;

    /**
     * Default text for RESPONSE_OPTIONS_TEXT.
     *
     * This field allows the user to specify a default value that will appear in the CRF section the first time the user
     * accesses the form.  For single-select default value does not have to be part of the response set and can be
     * instructive text if need be.  It will be interpreted as a blank value if the user does not choose anything.
     *
     * Default values can be used for the following RESPONSE_TYPEs:
     * <ul>
     * <li>TEXT</li>
     * <li>TEXTAREA</li>
     * <li>SINGLE-SELCT</li>
     * <li>MULTI-SELECT</li>
     * <li>CHECKBOX</li>
     * </ul>
     *
     * Default values can not be used for the following RESPONSE_TYPEs (CRF will be rejected on upload):
     * <ul>
     * <li>CALCULATION</li>
     * <li>GROUP_CALCULATION</li>
     * <li>FILE</li>
     * <li>INSTANT_CALCULATION</li>
     * <li>RADIO</li>
     * </ul>
     *
     * Be careful in using this field because if the default value corresponds to an option in the response set, it will
     * be saved to the database even if the user does not select it.
     */
    @Size(max = 4000) private String defaultValue;

    /**
     * Specify the width (the length of the field) and the number of decimal places to use for the field.
     * If provided must be in the form w(d) as follows:
     *
     * w – integer from 1 to 26, or literal ‘w’ if INT or REAL.  If ST, from 1 to 4000 is allowed.
     *
     * d – literal ‘d’. if the item has DATA_TYPE of ‘REAL’, may also be an integer from 1 to 20. d cannot be larger than w
     *
     *
     * Defines the width (the maximum allowed length of the field) and the number of decimal places to use for the field
     * in the form w(d).
     *
     * The first input defines the width of the field. The second input specifies the number of decimal places for
     * the field, if the item has a DATA_TYPE of ‘REAL’.
     *
     * The WIDTH_DECIMAL attribute should only be used for items with the ST, INT or REAL data types. The width
     * attribute specifies the length of the field treated as a string, so even if the item is of type INT or REAL,
     * leading/trailing zeroes and decimal points count towards the width.
     *
     * For items of type REAL, evaluation of the width occurs prior to evaluation of the decimal, so values exceeding
     * the specified or system default width will be rejected even if they could be rounded to a length within
     * the width limit.
     *
     * Examples.:
     * DATA_TYPE ‘REAL’, WIDTH_DECIMAL 5(1) – Allows a maximum of 5 characters with only 1 decimal place. OpenClinica
     * will accept 12345 and 1234., 123.4, or 12.30  but will not accept 012345 or 123456.
     *
     * Inputs such as 12.345 or 1234.5678 or 012345 or 12.300 will be allowed and rounded.
     *
     * REAL w(4) –Allows up to OpenClinica’s maximum length for an item of ST, INT, or REAL (26 characters), with
     * any decimal in excess of 1/10000th rounded to the 4th decimal place.
     *
     * REAL 20(d) –Allows a maximum length of 20 and decimal length of 4 (the default in OpenClinica).
     *
     * ST 20(d) or INT 20(d) – Allows a maximum length of 20 characters.
     *
     * If the DATA_TYPE of the item is DATE, PDATE, or FILE, the WIDTH_DECIMAL attribute should be left blank.
     *
     * Please be advised that OpenClinica is not tuned to process very large REAL numbers or numbers with many digits
     * after decimal point. So, numbers like 1234.123456789012345589 may not be validated properly to their format.
     * For more complex scenarios where precision above 20 digits is required, it may be better to use a regular
     * expression to verify the input.
     *
     */
    @Pattern(regexp = WIDTH_DECIMAL_PATTERN) private String widthDecimal;

    /**
     * Specify a validation expression to run an edit check on this item at the point of data entry.
     *
     * The validation will run when the user hits 'save'. If the user has entered data, which satisfy the validation
     * expression, data will save normally. If the value entered does not meet the requirements of the validation,
     * an error message will appear (i.e., the VALIDATION_ERROR_MESSAGE) and the user will need to correct the value
     * or enter a discrepancy note to continue. The validation should be of the format "expressionType: expression".
     * Must be between 1 and 1000 characters and is an optional field.
     *
     * regexp:
     *
     * This Supports Java-style regular expressions (similar to Perl). For more information,
     * see http://java.sun.com/j2se/1.4.2/docs/api/java/util/regex/Pattern.html
     *
     * Examples:
     * This example requires a three-letter string (all uppercase)
     * regexp: /regular expression/ = regexp: /[A-Z]{3}/
     *
     * func:
     *
     * Supports built-in library of basic integer functions. Currently supported functions include:
     * (1) greater than - gt(int) or gt(real)
     * (2) less than - lt(int) or lt(real)
     * (3) range - range(int1, int2) or range(real1, real2)
     * (4) gte(int) or gte(real)
     * (5) lte(int) or lte(real)
     * (6) ne(int) or ne(real)
     * (7) eq(int) or eq(real)
     *
     * Examples:
     * This example requires a number between 1 and 10
     * func: func(args) = func: range(1, 10)
     */
    @Size(min = 1, max = 1000) private String validation;

    /**
     * Defines an error message provided to on the data entry screen when a user enters data that does not meet
     * the VALIDATION.
     *
     * Must be used when a VALIDATION is specified and it should be clear to the data entry person what the problem is.
     * If there is a VALIDATION stating the number must be between 1-10, write that message in this field for the user
     * to see if they enter 11 or 0.
     */
    @Size(min = 1, max = 255) private String validationErrorMessage;

    /**
     * Signifies whether this item would be considered Protected Health Information.
     *
     * "Leaving the field blank or selecting 0 means the item would not be considered Protected Health Information.
     * This flag does not do anything to mask the data or prevent people from seeing it.
     * The field is used as a label only.
     *
     * When creating a data set, this label will show in the metadata and the user could choose to include this item
     * in the dataset (create dataset step) or not based on this label.
     *
     * This field should not be changed in any subsequent versions of the CRF. If you do change it and you are
     * an owner of the CRF the PHI attribute for this item will be changed for all versions of the CRF.
     */
    @Size(min = 0, max = 1) private Integer phi;

    /**
     * This field determines whether the user must provide a value for it before saving the section the item appears in.
     *
     * Leaving the field blank or selecting 0 means the item would be optional so the data entry person does not have to
     * provide a value for it.  If 1 is selected, the data entry person must provide a value, or enter a discrepancy
     * note explaining why the field is left blank. This can be used for any RESPONSE_TYPE
     *
     */
    @Size(min = 0, max = 1) private Integer required;

    /**
     * Used in conjunction with Dynamics in Rules or SIMPLE_CONDITIONAL_DISPLAY. If set to HIDE, the item will not
     * appear in the CRF when a user is entering data unless certain conditions are met. The conditions for display are
     * specified with a Rule using the ShowAction, or via SIMPLE_CONDITIONAL_DISPLAY. If left blank, the value defaults
     * to SHOW.
     *
     * If you would like to design skip patterns and dynamic logic for a single item, set the display status to HIDE.
     *
     * When the form is accessed for data entry, the item will initially be hidden from view from the user.  With Rules,
     * another value can trigger the group of items to be shown instead of hidden.
     *
     * Instead of Rules, you can also use the SIMPLE_CONDITIONAL_DISPLAY field to decide when this item should be shown.
     * SIMPLE_CONDITIONAL_DISPLAY only works with items set to HIDE.
     */
    @NotNull private DisplayStatus displayStatus;

    /**
     * Contains 3 parts, all separated by a comma:  ITEM_NAME, RESPONSE_VALUE, Message.
     *
     * ITEM_NAME - The item name of the field determining whether this hidden item becomes shown.
     * RESPONSE_VALUE - The value of the ITEM_NAME that will trigger this hidden item to be shown
     * Message - A validation message that will be displayed if this item has a value but should not be shown anymore.
     *
     * Simple Conditional Display works with items that have a defined response set (radio, checkbox, multi-select and
     * single-select fields).  The hidden item can be of any response type.
     *
     * SIMPLE_CONDITIONAL_DISPLAY (SCD) has an effect only when ITEM_DISPLAY_STATUS (IDS) of the item is set to HIDE.
     * Several levels of hierarchy of Simple Conditional fields can be nested hierarchically. The items must be in the
     * same section of the CRF
     *
     * For example, assume there is a SEX item with response options of Male, Female, and response values of 1,2.
     * If the user chooses Female option, additional questions about pregnancy are asked. If Male is chosen,
     * these questions are hidden. However, if the user chooses Female, fills in pregnancy data and after that gets back
     * to the SEX item and switches the answer to Male, the items about pregnancy will remain on the screen (not hidden).
     * The user can delete pregnancy answers and in that case the UI items will get hidden.
     *
     * Note that the database gets updated only on SAVE. In the above example the system will allow saving
     * “inconsistent” data (SEX = Male, but pregnancy items filled), but it is up to a user to create discrepancy
     * fields for them explaining the situation.
     *
     * Note that radio button controls cannot be deselected, meaning there is no way to delete it’s value once it has
     * been selected.
     *
     */
    private String simpleConditionalDisplay;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptionLabel() {
        return descriptionLabel;
    }

    public void setDescriptionLabel(String descriptionLabel) {
        this.descriptionLabel = descriptionLabel;
    }

    public String getLeftItemText() {
        return leftItemText;
    }

    public void setLeftItemText(String leftItemText) {
        this.leftItemText = leftItemText;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getRightItemText() {
        return rightItemText;
    }

    public void setRightItemText(String rightItemText) {
        this.rightItemText = rightItemText;
    }

    public Section getSection() {
        return section;
    }

    void setSection(Section section) {
        this.section = section;
    }

    public Group getGroup() {
        return group;
    }

    void setGroup(Group group) {
        this.group = group;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getSubheader() {
        return subheader;
    }

    public void setSubheader(String subheader) {
        this.subheader = subheader;
    }

    public Item getParent() {
        return parent;
    }

    void setParent(Item parent) {
        this.parent = parent;
    }

    public Integer getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(Integer columnNumber) {
        this.columnNumber = columnNumber;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(String questionNumber) {
        this.questionNumber = questionNumber;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    public String getResponseLabel() {
        return responseLabel;
    }

    public void setResponseLabel(String responseLabel) {
        this.responseLabel = responseLabel;
    }

    public String getResponseOptionsText() {
        return responseOptionsText;
    }

    public String getResponseValuesOrCalculations() {
        return responseValuesOrCalculations;
    }

    public void setResponseOptions(Iterable<ResponseOption> options) {
        List<String> texts = new ArrayList<String>();
        List<String> values = new ArrayList<String>();

        for (ResponseOption option : options) {
            option.setItem(this);
            texts.add(option.getText());
            values.add(option.getValue());
        }

        this.responseOptionsText = storeResponseOptions(texts);
        this.responseValuesOrCalculations = storeResponseOptions(values);
    }

    public List<ResponseOption> getResponseOptions() {
        List<String> texts = parseResponseOptions(this.responseOptionsText);
        List<String> values = parseResponseOptions(this.responseValuesOrCalculations);
        List<ResponseOption> options = new ArrayList<ResponseOption>();

        for (int i = 0; i < texts.size(); i++) {
            options.add(new ResponseOption(this, texts.get(i), values.get(i)));
        }

        return options;
    }

    public ResponseLayout getResponseLayout() {
        return responseLayout;
    }

    public void setResponseLayout(ResponseLayout responseLayout) {
        this.responseLayout = responseLayout;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getWidthDecimal() {
        return widthDecimal;
    }

    public void setWidthDecimal(String widthDecimal) {
        this.widthDecimal = widthDecimal;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public String getValidationErrorMessage() {
        return validationErrorMessage;
    }

    public void setValidationErrorMessage(String validationErrorMessage) {
        this.validationErrorMessage = validationErrorMessage;
    }

    public Integer getPhi() {
        return phi;
    }

    public void setPhi(Integer phi) {
        this.phi = phi;
    }

    public Integer getRequired() {
        return required;
    }

    public void setRequired(Integer required) {
        this.required = required;
    }

    public DisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(DisplayStatus displayStatus) {
        this.displayStatus = displayStatus;
    }

    public String getSimpleConditionalDisplay() {
        return simpleConditionalDisplay;
    }

    public ConditionalDisplay getConditionalDisplay() {
        if (simpleConditionalDisplay == null) {
            return null;
        }
        if (section == null) {
            return null;
        }
        List<String> parts = parseResponseOptions(simpleConditionalDisplay);

        Item item = getSection().getItems().get(parts.get(0));

        ResponseOption option = null;
        for (ResponseOption responseOption : item.getResponseOptions()) {
            if (parts.get(1).equals(responseOption.getValue())) {
                option = responseOption;
                break;
            }
        }

        return new ConditionalDisplay(option, parts.get(2));
    }

    public void setConditionalDisplay(ConditionalDisplay conditionalDisplay) {
        List<String> values = new ArrayList<String>();
        values.add(conditionalDisplay.getResponse().getItem().getName());
        values.add(conditionalDisplay.getResponse().getValue());
        values.add(conditionalDisplay.getMessage());
        this.simpleConditionalDisplay = storeResponseOptions(values);
    }
}