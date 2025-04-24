package cool.drinkup.drinkup.workflow.model;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "user_wine")
@Data
public class UserWine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    private String name;
    private String nameEn;
    private String description;
    private String baseIngredient;
    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String ingredients;
    private String ingredientOverview;
    private String ingredientAlcoholOverview;
    private String method;
    private String garnish;
    private String glassware;
    private String flavor;
    private String mixology;
    private String preparationTime;
    private String alcoholContent;
    private String calories;
    private String invented;
    private String tagMainBaseSpirit;
    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String tagBaseSpirit;
    private String tagGlass;
    private String tagMixology;
    private String tagCocktailType;
    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String tagFlavor;
    private String tagComplexity;
    private String tagAbv;
    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String tagsOthers;
    private String image;
} 