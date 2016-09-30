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
package org.knime.knip.base.nodes.filter;

import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.knip.base.node.ImgPlusToImgPlusNodeFactory;
import org.knime.knip.base.node.dialog.DescriptionHelper;
import org.knime.knip.base.node.dialog.Descriptions;
import org.knime.knip.core.types.NeighborhoodType;
import org.knime.knip.core.types.OutOfBoundsStrategyEnum;
import org.knime.node.v210.FullDescriptionDocument.FullDescription;
import org.knime.node.v210.KnimeNodeDocument.KnimeNode;
import org.knime.node.v210.OptionDocument.Option;
import org.knime.node.v210.TabDocument.Tab;

import net.imglib2.type.numeric.RealType;

/**
 *
 * @author <a href="mailto:dietzc85@googlemail.com">Christian Dietz</a>
 * @author <a href="mailto:horn_martin@gmx.de">Martin Horn</a>
 * @author <a href="mailto:michael.zinsmaier@googlemail.com">Michael Zinsmaier</a>
 */
public abstract class SlidingWindowOperationNodeFactory<T extends RealType<T>, V extends RealType<V>> extends
        ImgPlusToImgPlusNodeFactory<T, V> {

    protected static SettingsModelInteger createBoxExtendModel() {
        return new SettingsModelInteger("radius", 3);
    }

    protected static SettingsModelString createNeighborhoodTypeNodeModel() {
        return new SettingsModelString("neighborhood_type", NeighborhoodType.RECTANGULAR.toString());
    }

    protected static SettingsModelString createOutOfBoundsModel() {
        return new SettingsModelString("outofboundsstrategy", OutOfBoundsStrategyEnum.BORDER.toString());
    }

    protected static SettingsModelBoolean createSpeedUpModel() {
        return new SettingsModelBoolean("speedUp", true);
    }

    protected final String INTEGRAL_IMAGE_SPEED_UP_TEXT =
            "Activates the usage of integral images. If the sum of all pixels is smaller than the internal type of the integral image overflows will occur.";

    protected final String NO_SPEED_UP_TEXT = "No speed up method used";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addNodeDescriptionContent(final KnimeNode node) {
        addSlidingWindowParametersTo(node.getFullDescription());




        int index = DescriptionHelper.findTabIndex("Options", node.getFullDescription().getTabList());
        Descriptions.createNodeDescriptionOutOfBounds(node.getFullDescription().getTabArray(index)
                .addNewOption());
        Descriptions.createNodeDescriptionSpan(node.getFullDescription().getTabArray(index).addNewOption());

        super.addNodeDescriptionContent(node);
    }

    /**
     * searches the provided node description for a tab with the name "options" generates such a tab if it doesn't
     * exist.
     *
     * @param fdesc
     */
    private void addSlidingWindowParametersTo(final FullDescription fdesc) {
        Tab optionTab = null;
        for (final Tab t : fdesc.getTabList()) {
            if (t.getName().equals("Options")) {
                optionTab = t;
            }
        }

        if (optionTab == null) {
            optionTab = fdesc.addNewTab();
            optionTab.setName("Options");
        }

        addSlidingWindowParametersTo(optionTab);
    }

    /**
     * adds descriptions for the standard sliding window parameters like out of bounds strategy and neighborhood type.
     * Also adds the descriptions for potential Speed Up strategies {@link #getSpeedUpOptionText()}.
     *
     * @param optionTab a handle for the "options" tab. The overloaded method
     *            {@link #addSlidingWindowParametersTo(FullDescription)} can be used to get this handle.
     */
    private void addSlidingWindowParametersTo(final Tab optionTab) {

        Option op = optionTab.addNewOption();

        op.setName("Speed Up");
        op.addNewP().newCursor().setTextValue(getSpeedUpOptionText());

        // Handled in addNodeDescription

        //        op = optionTab.addNewOption();
        //        op.setName("Out of bounds strategy");
        //        op.addNewP()
        //                .newCursor()
        //                .setTextValue("Specifies which values the algorithm should use "
        //                                      + "outside the image's bounds. Possible options are: Border (out of "
        //                                      + "bounds value is always 0), Mirror_Single, Mirror_Double (virtually "
        //                                      + "mirrors the image at its boundaries. Boundary pixels are either "
        //                                      + "duplicated or not");

        op = optionTab.addNewOption();
        op.setName("Neighborhood Type");
        op.addNewP().newCursor().setTextValue("Select a rectangle or circle neighborhood as sliding window.");
    }

    /**
     * @return either something like "no speed up method used" or a description of the implemented speed up method and
     *         potential numerical ... errors that come with it.
     */
    protected abstract String getSpeedUpOptionText();

}
