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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cesar Coelho
 */
public class SlicePanel extends javax.swing.JPanel {

    private String folderPrefix;
    private final NMFPackageAssemblerGUI parentFrame;
    private final ArrayList<FilesSourceObject> sources;

    /**
     * Creates new form ConsumerPanelArchive
     *
     * @param parentFrame The parent panel
     * @param type The type, can be an application or a library
     * @param name The name of the application or library
     * @param sources The sources of the application or library
     */
    public SlicePanel(final NMFPackageAssemblerGUI parentFrame,
            final String type, final String name, ArrayList<FilesSourceObject> sources) {
        initComponents();

        if (sources == null) {
            this.sources = new ArrayList<FilesSourceObject>();
        } else {
            this.sources = sources;
        }

        this.parentFrame = parentFrame;

        if (type.equals(NMFPackageAssemblerGUI.TYPE_APPLICATION)) {
            this.folderPrefix = "apps" + File.separator + name;
        }

        if (type.equals(NMFPackageAssemblerGUI.TYPE_LIBRARY)) {
            this.folderPrefix = "libs" + File.separator + name;
        }

        this.type.setText(type + ":");
        this.value.setText(name);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        type = new javax.swing.JLabel();
        addOrModifyFiles = new javax.swing.JButton();
        deleteEntry = new javax.swing.JButton();
        value = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(1000, 80));
        setPreferredSize(new java.awt.Dimension(600, 80));
        setRequestFocusEnabled(false);

        type.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        type.setText("Application or Library:");
        type.setPreferredSize(new java.awt.Dimension(150, 14));

        addOrModifyFiles.setText("Add or Modify Files");
        addOrModifyFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addOrModifyFilesActionPerformed(evt);
            }
        });

        deleteEntry.setText("Delete entry");
        deleteEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteEntryActionPerformed(evt);
            }
        });

        value.setText("Name of it");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(type, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(value, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(addOrModifyFiles, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(deleteEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(value)
                    .addComponent(addOrModifyFiles))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteEntry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addOrModifyFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addOrModifyFilesActionPerformed
        try {
            AddModifyFiles files = new AddModifyFiles(folderPrefix, sources);
        } catch (IOException ex) {
            Logger.getLogger(SlicePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_addOrModifyFilesActionPerformed

    private void deleteEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteEntryActionPerformed
        parentFrame.removeEntry(this);
    }//GEN-LAST:event_deleteEntryActionPerformed

    public ArrayList<FilesSourceObject> getSources() {
        return sources;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addOrModifyFiles;
    private javax.swing.JButton deleteEntry;
    private javax.swing.JLabel type;
    private javax.swing.JLabel value;
    // End of variables declaration//GEN-END:variables

}
