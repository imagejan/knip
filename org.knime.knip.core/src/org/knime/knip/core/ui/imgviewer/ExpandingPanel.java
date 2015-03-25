package org.knime.knip.core.ui.imgviewer;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import org.knime.knip.core.ui.event.EventService;

public class ExpandingPanel extends ViewerComponent {

	private JLabel m_header;

	private ViewerComponent m_content;

	private boolean isExpanded = false;

	public ExpandingPanel(final String name, final ViewerComponent content) {
	    super("", false);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;
		gbc.weighty = 0;


		m_header = new JLabel(name, SwingConstants.CENTER);
		m_header.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		m_header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
		m_header.setMinimumSize(new Dimension(150, 25));
		m_header.setPreferredSize(m_header.getMinimumSize());
		add(m_header, gbc);
		m_header.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(final MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(final MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(final MouseEvent e) {
				m_content.setVisible(!m_content.isVisible());
				isExpanded = !isExpanded;
				ExpandingPanel.super.repaint();

			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;
		gbc.weighty = 1;

		m_content = content;
		m_content.setVisible(false);
		add(content, gbc);
		setVisible(true);

	}

	@Override
	public Dimension getMaximumSize() {
		if (isExpanded){
			Dimension s = new Dimension(m_header.getMaximumSize().width,
					250);
			return s;
		} else {
            return m_header.getMaximumSize();
        }
	}

	@Override
	public Dimension getMinimumSize() {
		if (isExpanded) {
            return new Dimension(Math.max(m_header.getMinimumSize().width,
					m_content.getMinimumSize().width),
					m_header.getMinimumSize().height
							+ m_content.getMinimumSize().height);
        } else {
            return m_header.getMinimumSize();
        }
	}

	@Override
	public Dimension getPreferredSize() {
		if (isExpanded)
		{
			int contentwidth = m_content.getPreferredSize().width;
			int headerwidth = m_header.getPreferredSize().width;

			return new Dimension(Math.max(Math.min(contentwidth, headerwidth), headerwidth),
					m_header.getPreferredSize().height
							+ m_content.getPreferredSize().height);
		} else {
            return m_header.getPreferredSize();
        }
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEventService(final EventService eventService) {
        m_content.setEventService(eventService);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Position getPosition() {
        // TODO Auto-generated method stub
        return Position.ADDITIONAL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveComponentConfiguration(final ObjectOutput out) throws IOException {
        m_content.saveComponentConfiguration(out);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadComponentConfiguration(final ObjectInput in) throws IOException, ClassNotFoundException {
        m_content.loadComponentConfiguration(in);

    }

}