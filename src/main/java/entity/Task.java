/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "TASK")
public class Task implements Serializable {

    @ManyToOne
    private SemesterPeriod semesterPeriod;
    private static final long serialVersionUID = 1L;
    @Id
    //@GeneratedValue(strategy = GenerationType.TABLE,generator = "idGenerator")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private int maxScore;
    @OneToMany(mappedBy = "task", cascade = {CascadeType.PERSIST, CascadeType.REMOVE,CascadeType.ALL})
    private final List<StudyPoint> studyPoints = new ArrayList();

    public Task() {
    }

    public Task(int maxScore, String name, SemesterPeriod period) {
        this.maxScore = maxScore;
        this.name = name;
        this.semesterPeriod = period;
    }

    public List<StudyPoint> getStudyPoints() {
        return studyPoints;
    }

    public StudyPoint getStudyPointForStudent(int studentId) {
        for (StudyPoint sp : studyPoints) {
            if (sp.getStudyPointUser().getId() == studentId) {
                return sp;
            }
        }
        return null;
    }

    public void addStudyPoint(StudyPoint sp) {
        studyPoints.add(sp);
    }

    public SemesterPeriod getSemesterPeriod() {
        return semesterPeriod;
    }

    public void setSemesterPeriod(SemesterPeriod semesterPeriod) {
        this.semesterPeriod = semesterPeriod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
