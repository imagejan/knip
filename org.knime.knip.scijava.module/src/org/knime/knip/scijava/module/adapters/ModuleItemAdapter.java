package org.knime.knip.scijava.module.adapters;

import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataValue;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.knip.scijava.module.dialog.DialogComponentGroup;
import org.scijava.module.Module;
import org.scijava.module.ModuleItem;

public interface ModuleItemAdapter<D extends DataValue> {

	Class<D> getDataValueType();

	void configureItem(final ModuleItem<?> item, final DataTableSpec spec,
			final DataRow row, Module module, final SettingsModel model);

	DialogComponentGroup getDialogComponentGroup(ModuleItem<?> item);

	SettingsModel createSettingsModel(ModuleItem<?> item);

}
