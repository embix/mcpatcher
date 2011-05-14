package com.pclewis.mcpatcher;

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipTreeDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel treePanel;
    private JTree tree;

    private ZipFile zipFile;
    private String prefix;

    public ZipTreeDialog(final ZipFile zipFile, String prefix) {
        this.zipFile = zipFile;
        this.prefix = prefix;

        setContentPane(contentPane);
        setTitle("Select folder to add to minecraft.jar");
        setMinimumSize(new Dimension(384, 384));
        setResizable(true);
        setModal(true);
        pack();
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        tree.setModel(new TreeModel() {
            public Object getRoot() {
                return new MyTreeNode("/", "");
            }

            public Object getChild(Object parent, int index) {
                return getChildren(parent).get(index);
            }

            public int getChildCount(Object parent) {
                return getChildren(parent).size();
            }

            public boolean isLeaf(Object node) {
                return false;
            }

            public void valueForPathChanged(TreePath path, Object newValue) {
            }

            public int getIndexOfChild(Object parent, Object child) {
                return getChildren(parent).indexOf((MyTreeNode) child);
            }

            public void addTreeModelListener(TreeModelListener l) {
            }

            public void removeTreeModelListener(TreeModelListener l) {
            }

            private ArrayList<MyTreeNode> getChildren(Object object) {
                String parent = ((MyTreeNode) object).path;
                ArrayList<MyTreeNode> list = new ArrayList<MyTreeNode>();
                for (ZipEntry entry : Collections.list(zipFile.entries())) {
                    String name = entry.getName();
                    if (entry.isDirectory() && name.startsWith(parent)) {
                        String suffix = name.substring(parent.length()).replaceFirst("/$", "");
                        if (!suffix.equals("") && !suffix.contains("/")) {
                            list.add(new MyTreeNode(suffix, name));
                        }
                    }
                }
                return list;
            }

            class MyTreeNode {
                String label;
                String path;

                MyTreeNode(String label, String path) {
                    this.label = label;
                    this.path = path;
                }

                public String toString() {
                    return label;
                }

                public int compareTo(MyTreeNode node) {
                    return path.compareTo(node.path);
                }
            }
        });
        tree.setRootVisible(true);
        tree.setExpandsSelectedPaths(true);
        tree.setSelectionPath(new TreePath("/"));
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}