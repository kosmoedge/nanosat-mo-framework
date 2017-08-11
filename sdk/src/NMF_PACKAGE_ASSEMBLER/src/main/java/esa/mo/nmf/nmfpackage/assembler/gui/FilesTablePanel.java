/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * ----------------------------------------------------------------------------
 */
package esa.mo.nmf.nmfpackage.assembler.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Cesar Coelho
 */
public final class FilesTablePanel extends javax.swing.JPanel {

    private final DefaultTableModel filesTableData;

    /**
     * Creates new form ObjectsDisplay
     *
     */
    public FilesTablePanel() {
        initComponents();

        String[] archiveTableCol = new String[]{"Files", "From"};

        filesTableData = new javax.swing.table.DefaultTableModel(
                new Object[][]{}, archiveTableCol) {
            Class[] types = new Class[]{
                java.lang.String.class, java.lang.String.class
            };

            @Override               //all cells false
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };

        table.setModel(filesTableData);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    /*
                    // Get from the list of objects the one we want and display
                    ArchivePersistenceObject comObject = getSelectedCOMObject();
                    try {
                        COMObjectWindow comObjectWindow = new COMObjectWindow(comObject, false, archiveService.getArchiveStub());
                    } catch (IOException ex) {
                        Logger.getLogger(FilesTablePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                     */
                }
            }
        });

//        this.addEntries(archiveObjectOutput);
    }

    public int getSelectedRow() {
        return table.getSelectedRow();
    }

    public void removeSelectedEntry() {
        filesTableData.removeRow(this.getSelectedRow());
    }

    public void removeAllEntries() {
        while (filesTableData.getRowCount() != 0) {
            filesTableData.removeRow(filesTableData.getRowCount() - 1);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();

        jScrollPane3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane3.setHorizontalScrollBar(null);
        jScrollPane3.setPreferredSize(new java.awt.Dimension(796, 380));
        jScrollPane3.setRequestFocusEnabled(false);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Domain", "Object Type", "Obj Instance Id", "Timestamp", "Related", "Source"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        table.setAlignmentX(0.0F);
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setAutoscrolls(false);
        table.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        table.setMaximumSize(null);
        table.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                tableComponentAdded(evt);
            }
        });
        jScrollPane3.setViewportView(table);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tableComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_tableComponentAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_tableComponentAdded


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
