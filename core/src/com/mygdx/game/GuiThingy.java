package com.mygdx.game;

import com.badlogic.gdx.scenes.scene2d.Group;
import datastructure.ReorderableArrayList;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GuiThingy extends JFrame {
    private JComboBox<CharacterActor> groupComboBox;
    private JComboBox<DirectionCharacterActor> actorComboBox;
    private JComboBox<MyActor> partsComboBox;
    private JTextField xField, yField, widthField, heightField, originXField, originYField, scaleField;
    private JButton applyButton;

    private ReorderableArrayList<CharacterActor> groups;
    private CharacterActor selectedGroup;
    private DirectionCharacterActor selectedActor;
    private MyActor selectedBodyPart;

    public GuiThingy(ReorderableArrayList<CharacterActor> groups) {
        this.groups = groups;
        if (this.groups != null) {
            System.out.println("Groups:"+groups);
        }
        setTitle("Actor Properties Editor");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        groupComboBox = new JComboBox<>(groups.toArray(new CharacterActor[groups.size()]));
        groupComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedGroup = (CharacterActor) groupComboBox.getSelectedItem();
                updateActorComboBox();
            }
        });
        if (!groups.isEmpty()) {
            selectedGroup = groups.get(0);
        }
        initActorComboBox();
        actorComboBox = new JComboBox<>();
        actorComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedActor = (DirectionCharacterActor) actorComboBox.getSelectedItem();
                updatePartsComboBox();

            }
        });
        initPartsComboBox();

        partsComboBox = new JComboBox<>();
        partsComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedBodyPart = (MyActor) partsComboBox.getSelectedItem();
                updateFields();
            }
        });
        updateFields();

        xField = new JTextField();
        yField = new JTextField();
        widthField = new JTextField();
        heightField = new JTextField();
        originXField = new JTextField();
        originYField = new JTextField();
        scaleField = new JTextField();

        applyButton = new JButton("Apply");
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyChanges();
            }
        });

        add(new JLabel("Group:"));
        add(groupComboBox);
        add(new JLabel("Actor:"));
        add(actorComboBox);
        add(new JLabel("Part:"));
        add(partsComboBox);
        add(new JLabel("X:"));
        add(xField);
        add(new JLabel("Y:"));
        add(yField);
        add(new JLabel("Width:"));
        add(widthField);
        add(new JLabel("Height:"));
        add(heightField);
        add(new JLabel("Origin X:"));
        add(originXField);
        add(new JLabel("Origin Y:"));
        add(originYField);
        add(new JLabel("Scale:"));
        add(scaleField);
        add(applyButton);

        if (!groups.isEmpty()) {
            selectedGroup = groups.get(0);
            updateActorComboBox();
        }
    }

    private void updateActorComboBox() {
        if (actorComboBox != null) {
            actorComboBox.removeAllItems();
        }
        if (selectedGroup != null) {
            System.out.println("selectedGroup not null");
            Integer first = -1;
            for(DirectionCharacterActor entry: selectedGroup.directions.values()){
                if (first < 0) {
                    first = 0;
                    selectedActor = entry;
                }
                actorComboBox.addItem(entry);
            }
            if (selectedGroup.directions.size() > 0 && first>=0) {
                updatePartsComboBox();
            }
        }
    }
    private void initActorComboBox() {
        if (selectedGroup != null) {
            System.out.println("selectedGroup not null");
            Integer first = -1;
            for(DirectionCharacterActor entry: selectedGroup.directions.values()){
                if (first < 0) {
                    first = 0;
                    selectedActor = entry;
                }
            }
        }
    }
    private void updatePartsComboBox() {
        if (partsComboBox != null) {
            partsComboBox.removeAllItems();
        }
        if (selectedActor != null) {
            Integer first =-1;
            // partsComboBox.addItem(selectedGroup);
            for (MyActor actor : selectedActor.bodyPartMap.values()) {
                if (first < 0) {
                    first = 0;
                    selectedBodyPart = actor;
                }
                partsComboBox.addItem(actor);
            }
            if (selectedActor.parts.length > 0) {
                updateFields();
            }
        }
    }
    private void initPartsComboBox() {
        Integer first =-1;
        for (MyActor actor : selectedActor.bodyPartMap.values()) {
            if (first < 0) {
                first = 0;
                selectedBodyPart = actor;
            }
        }
    }

    private void updateFields() {
        if (selectedBodyPart != null && xField != null) {
            xField.setText(Float.toString(selectedBodyPart.getX()));
            yField.setText(Float.toString(selectedBodyPart.getY()));
            widthField.setText(Float.toString(selectedBodyPart.getWidth()));
            heightField.setText(Float.toString(selectedBodyPart.getHeight()));
            originXField.setText(Float.toString(selectedBodyPart.getOriginX()));
            originYField.setText(Float.toString(selectedBodyPart.getOriginY()));
            scaleField.setText(Float.toString(selectedBodyPart.getScaleX())); // Assuming uniform scale
        }
    }

    private void applyChanges() {
        if (selectedBodyPart != null) {
            selectedBodyPart.setPosition(Float.parseFloat(xField.getText()), Float.parseFloat(yField.getText()));
            selectedBodyPart.setSize(Float.parseFloat(widthField.getText()), Float.parseFloat(heightField.getText()));
            selectedBodyPart.setOrigin(Float.parseFloat(originXField.getText()), Float.parseFloat(originYField.getText()));
            selectedBodyPart.setScaleX(Float.parseFloat(scaleField.getText()));
        }
    }
}
