/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2013
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * --------------------------------------------------------------------- *
 *
 */
package org.knime.knip.base.nodes.seg.morphops;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialog;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.knip.base.data.img.ImgPlusValue;
import org.knime.knip.base.data.labeling.LabelingValue;
import org.knime.knip.base.node.ValueToCellNodeDialog;
import org.knime.knip.base.node.dialog.DialogComponentDimSelection;
import org.knime.knip.base.nodes.seg.morphops.MorphLabelingOpsNodeModel.LabelHandling;
import org.knime.knip.base.nodes.seg.morphops.MorphLabelingOpsNodeModel.MorphOp;

/**
 * {@link NodeDialog} for Morphological Labeling Operations
 *
 * @author <a href="mailto:dietzc85@googlemail.com">Christian Dietz</a>
 * @author <a href="mailto:horn_martin@gmx.de">Martin Horn</a>
 * @author <a href="mailto:michael.zinsmaier@googlemail.com">Michael Zinsmaier</a>
 *
 * @param <L>
 */
public class MorphLabelingOpsNodeDialog<L extends Comparable<L>> extends ValueToCellNodeDialog<LabelingValue<L>> {

    private SettingsModelString m_struct;

    private boolean m_structProvided;

    private SettingsModelString m_type;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void addDialogComponents() {
        m_type = MorphLabelingOpsNodeModel.createConnectionTypeModel();
        addDialogComponent("Options", "Structuring Element", new DialogComponentStringSelection(m_type,
                "Connection Type", MorphLabelingOpsNodeModel.ConnectedType.NAMES));
        m_struct = MorphLabelingOpsNodeModel.createStructureColModel();
        addDialogComponent("Options", "Structuring Element", new DialogComponentColumnNameSelection(m_struct,
                "Structuring Element", 1, false, true, ImgPlusValue.class));

        final SettingsModelString structColumn = m_struct;
        m_type.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                // activate column selection if structuring element was selected
                structColumn.setEnabled(isStructuringElement(m_type.getStringValue()));
            }
        });

        addDialogComponent("Options", "Operation",
                           new DialogComponentStringSelection(MorphLabelingOpsNodeModel.createOperationModel(), "",
                                   MorphOp.NAMES));
        addDialogComponent("Options", "Operation",
                           new DialogComponentStringSelection(MorphLabelingOpsNodeModel.createLabelingBasedModel(),
                                   "Strategy", LabelHandling.NAMES));

        addDialogComponent("Options", "Operation",
                           new DialogComponentNumber(MorphLabelingOpsNodeModel.createIterationsModel(),
                                   "Number of Iterations", 1));

        addDialogComponent("Options", "Dimension Selection",
                           new DialogComponentDimSelection(MorphLabelingOpsNodeModel.createDimSelectionModel(), "", 2,
                                   Integer.MAX_VALUE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveAdditionalSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        super.saveAdditionalSettingsTo(settings);

        if (MorphLabelingOpsNodeModel.ConnectedType.value(m_type.getStringValue()) == MorphLabelingOpsNodeModel.ConnectedType.STRUCTURING_ELEMENT
                && !m_structProvided) {
            throw new InvalidSettingsException(
                    "You can't have a strucuturing element selection without providing a strucuturing element in the second in port!");
        }

    }

    private boolean isStructuringElement(final String type) {
        return type == null
                || MorphLabelingOpsNodeModel.ConnectedType.value(type) == MorphLabelingOpsNodeModel.ConnectedType.STRUCTURING_ELEMENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadAdditionalSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs)
            throws NotConfigurableException {
        super.loadAdditionalSettingsFrom(settings, specs);
        m_structProvided = specs[1].getNumColumns() != 0;

        // if we dont have a structuring element anymore we select four connected as default
        if (!m_structProvided && m_struct.getStringValue() != null && isStructuringElement(m_type.getStringValue())) {
            // auto select something else
            m_type.setStringValue(MorphLabelingOpsNodeModel.ConnectedType.FOUR_CONNECTED.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultSuffixForAppend() {
        return "_morphLab";
    }
}
