package il.org.spartan.refactoring.preferences;

import java.util.*;
import java.util.List;

import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.preference.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/** A {@link FieldEditor} designed to store multiple controls within a group
 * panel widget, to be used in conjunction with an
 * {@link FieldEditorPreferencePage} instance.
 * <p>
 * <u>Usage:</u><br>
 * 1) Create a new {@link GroupFieldEditor} object.<br>
 * 2) Add {@link FieldEditor} objects using the
 * {@link GroupFieldEditor#add(FieldEditor)} method. Each {@link FieldEditor}
 * should be initialized to have the return value of
 * {@link GroupFieldEditor#getFieldEditor()} as its parent.<br>
 * 3) Add the {@link GroupFieldEditor} to the parent as usual
 * @author alf (original)
 * @author Daniel Mittelman (fixed and revised)
 * @since 29/03/2016 */
public class GroupFieldEditor extends FieldEditor {
  private static final int GROUP_PADDING = 8;
  private final String title;
  private int numColumns;
  private final List<FieldEditor> members = new ArrayList<>();
  private final Group group;
  private final Composite parent;
  private boolean initialized = false;

  /** Create a group of {@link FieldEditor} objects
   * @param labelText (optional) the text that will appear in the top label. For
   *        no label, pass {@code null}
   * @param fieldEditorParent the widget's parent, usually
   *        {@link FieldEditorPreferencePage#getFieldEditorParent()} */
  public GroupFieldEditor(final String labelText, final Composite fieldEditorParent) {
    title = labelText == null ? "" : labelText;
    parent = fieldEditorParent;
    numColumns = 0;
    group = new Group(parent, SWT.SHADOW_OUT);
    group.setText(title);
  }
  /** Adds a new {@link FieldEditor} object to the group. Controls must be added
   * before the group is drawn to the parent. */
  public void add(final FieldEditor e) {
    if (initialized)
      throw new RuntimeException("The GroupFieldEditor has already been drawn, new fields cannot be added at this time");
    members.add(e);
  }
  /* (non-Javadoc) Method declared on FieldEditor. */
  @Override protected void adjustForNumColumns(@SuppressWarnings("hiding") final int numColumns) {
    this.numColumns = numColumns;
  }
  /* (non-Javadoc) Method declared on FieldEditor. */
  @Override protected void doFillIntoGrid(final Composite parentParam, @SuppressWarnings("hiding") int numColumns) {
    if (members == null || members.isEmpty())
      return;
    if (numColumns == 0)
      for (final FieldEditor fe : members)
        numColumns = Math.max(numColumns, fe.getNumberOfControls());
    gridData(numColumns);
    gridLayout(numColumns);
    for (final FieldEditor ¢ : members)
      ¢.fillIntoGrid(parentParam, numColumns);
    parent.layout();
    parent.redraw();
  }
  /* (non-Javadoc) Method declared on FieldEditor. Loads the value from the
   * preference store and sets it to the check box. */
  @Override protected void doLoad() {
    for (final FieldEditor ¢ : members)
      ¢.load();
  }
  /* (non-Javadoc) Method declared on FieldEditor. Loads the default value from
   * the preference store and sets it to the check box. */
  @Override protected void doLoadDefault() {
    for (final FieldEditor ¢ : members)
      ¢.loadDefault();
  }
  /* (non-Javadoc) Method declared on FieldEditor. */
  @Override protected void doStore() {
    for (final FieldEditor ¢ : members)
      ¢.store();
  }
  /** Returns the parent for all the FieldEditors inside of this group. In this
   * class, the actual {@link Group} object is returned
   * @return the parent {@link Composite} object */
  public Composite getFieldEditor() {
    return group;
  }
  /* (non-Javadoc) Method declared on FieldEditor. */
  @Override public int getNumberOfControls() {
    return members.size();
  }
  private void gridData(final int n) {
    final GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
    data.horizontalIndent = 2;
    data.verticalIndent = GROUP_PADDING;
    data.horizontalSpan = n;
    group.setLayoutData(data);
  }
  private void gridLayout(final int n) {
    final GridLayout groupLayout = new GridLayout();
    groupLayout.marginWidth = groupLayout.marginHeight = GROUP_PADDING;
    groupLayout.numColumns = n;
    group.setLayout(groupLayout);
  }
  /** Initializes using the currently added field editors. */
  public void init() {
    if (initialized)
      return;
    doFillIntoGrid(getFieldEditor(), numColumns);
    initialized = true;
  }
  @Override public boolean isValid() {
    for (final FieldEditor ¢ : members)
      if (!¢.isValid())
        return false;
    return true;
  }
  /* @see FieldEditor.setEnabled */
  @Override public void setEnabled(final boolean enabled, final Composite parentParam) {
    for (final FieldEditor ¢ : members)
      ¢.setEnabled(enabled, parentParam);
  }
  /* (non-Javadoc) Method declared on FieldEditor. */
  @Override public void setFocus() {
    if (members != null && !members.isEmpty())
      members.iterator().next().setFocus();
  }
  @Override public void setPage(final DialogPage p) {
    for (final FieldEditor ¢ : members)
      ¢.setPage(p);
  }
  @Override public void setPreferenceStore(final IPreferenceStore s) {
    super.setPreferenceStore(s);
    for (final FieldEditor ¢ : members)
      ¢.setPreferenceStore(s);
  }
  @Override public void store() {
    doStore();
  }
}