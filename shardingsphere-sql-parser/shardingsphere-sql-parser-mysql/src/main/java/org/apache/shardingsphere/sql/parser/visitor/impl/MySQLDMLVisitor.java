/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.sql.parser.visitor.impl;

import org.apache.shardingsphere.sql.parser.api.visitor.DMLVisitor;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.AliasContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.AssignmentContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.AssignmentValueContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.AssignmentValuesContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.BlobValueContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.ColumnNamesContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.DeleteContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.DuplicateSpecificationContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.EscapedTableReferenceContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.ExprContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.FromClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.GroupByClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.InsertContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.InsertValuesClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.JoinSpecificationContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.JoinedTableContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.LimitClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.LimitOffsetContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.LimitRowCountContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.LockClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.MultipleTableNamesContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.MultipleTablesClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.OnDuplicateKeyClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.OrderByItemContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.ProjectionContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.ProjectionsContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.QualifiedShorthandContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.SelectClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.SelectContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.SelectSpecificationContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.SetAssignmentsClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.SingleTableClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.TableFactorContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.TableNameContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.TableReferenceContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.TableReferencesContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.UnionClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.UpdateContext;
import org.apache.shardingsphere.sql.parser.autogen.MySQLStatementParser.WhereClauseContext;
import org.apache.shardingsphere.sql.parser.sql.ASTNode;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.assignment.AssignmentSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.assignment.InsertValuesSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.assignment.SetAssignmentSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.column.ColumnSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.column.InsertColumnsSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.column.OnDuplicateKeyColumnsSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.expr.ExpressionSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.expr.complex.CommonExpressionSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.expr.complex.SubquerySegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.expr.simple.LiteralExpressionSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.item.AggregationProjectionSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.item.ColumnProjectionSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.item.ExpressionProjectionSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.item.ProjectionSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.item.ProjectionsSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.item.ShorthandProjectionSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.order.GroupBySegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.order.OrderBySegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.order.item.OrderByItemSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.pagination.PaginationValueSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.pagination.limit.LimitSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.pagination.limit.NumberLiteralLimitValueSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.pagination.limit.ParameterMarkerLimitValueSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.predicate.AndPredicate;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.predicate.LockSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.predicate.OrPredicateSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.predicate.PredicateSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.predicate.WhereSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.generic.AliasSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.generic.TableSegment;
import org.apache.shardingsphere.sql.parser.sql.statement.dml.DeleteStatement;
import org.apache.shardingsphere.sql.parser.sql.statement.dml.InsertStatement;
import org.apache.shardingsphere.sql.parser.sql.statement.dml.SelectStatement;
import org.apache.shardingsphere.sql.parser.sql.statement.dml.UpdateStatement;
import org.apache.shardingsphere.sql.parser.sql.value.collection.CollectionValue;
import org.apache.shardingsphere.sql.parser.sql.value.identifier.IdentifierValue;
import org.apache.shardingsphere.sql.parser.sql.value.literal.impl.BooleanLiteralValue;
import org.apache.shardingsphere.sql.parser.sql.value.literal.impl.NumberLiteralValue;
import org.apache.shardingsphere.sql.parser.sql.value.literal.impl.StringLiteralValue;
import org.apache.shardingsphere.sql.parser.sql.value.parametermarker.ParameterMarkerValue;
import org.apache.shardingsphere.sql.parser.visitor.MySQLVisitor;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * DML visitor for MySQL.
 */
public final class MySQLDMLVisitor extends MySQLVisitor implements DMLVisitor {
    
    @SuppressWarnings("unchecked")
    @Override
    public ASTNode visitInsert(final InsertContext ctx) {
        // TODO :FIXME, since there is no segment for insertValuesClause, InsertStatement is created by sub rule.
        InsertStatement result;
        if (null != ctx.insertValuesClause()) {
            result = (InsertStatement) visit(ctx.insertValuesClause());
        } else {
            result = new InsertStatement();
            result.setSetAssignment((SetAssignmentSegment) visit(ctx.setAssignmentsClause()));
        }
        if (null != ctx.onDuplicateKeyClause()) {
            result.setOnDuplicateKeyColumns((OnDuplicateKeyColumnsSegment) visit(ctx.onDuplicateKeyClause()));
        }
        result.setTable((TableSegment) visit(ctx.tableName()));
        result.setParametersCount(getCurrentParameterIndex());
        return result;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public ASTNode visitInsertValuesClause(final InsertValuesClauseContext ctx) {
        InsertStatement result = new InsertStatement();
        if (null != ctx.columnNames()) {
            ColumnNamesContext columnNames = ctx.columnNames();
            CollectionValue<ColumnSegment> columnSegments = (CollectionValue<ColumnSegment>) visit(columnNames);
            result.setInsertColumns(new InsertColumnsSegment(columnNames.start.getStartIndex(), columnNames.stop.getStopIndex(), columnSegments.getValue()));
        } else {
            result.setInsertColumns(new InsertColumnsSegment(ctx.start.getStartIndex() - 1, ctx.start.getStartIndex() - 1, Collections.<ColumnSegment>emptyList()));
        }
        result.getValues().addAll(createInsertValuesSegments(ctx.assignmentValues()));
        return result;
    }
    
    private Collection<InsertValuesSegment> createInsertValuesSegments(final Collection<AssignmentValuesContext> assignmentValuesContexts) {
        Collection<InsertValuesSegment> result = new LinkedList<>();
        for (AssignmentValuesContext each : assignmentValuesContexts) {
            result.add((InsertValuesSegment) visit(each));
        }
        return result;
    }
    
    @Override
    public ASTNode visitOnDuplicateKeyClause(final OnDuplicateKeyClauseContext ctx) {
        Collection<AssignmentSegment> columns = new LinkedList<>();
        for (AssignmentContext each : ctx.assignment()) {
            columns.add((AssignmentSegment) visit(each));
        }
        return new OnDuplicateKeyColumnsSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), columns);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public ASTNode visitUpdate(final UpdateContext ctx) {
        UpdateStatement result = new UpdateStatement();
        result.getTables().addAll(((CollectionValue<TableSegment>) visit(ctx.tableReferences())).getValue());
        result.setSetAssignment((SetAssignmentSegment) visit(ctx.setAssignmentsClause()));
        if (null != ctx.whereClause()) {
            result.setWhere((WhereSegment) visit(ctx.whereClause()));
        }
        result.setParametersCount(getCurrentParameterIndex());
        return result;
    }
    
    @Override
    public ASTNode visitSetAssignmentsClause(final SetAssignmentsClauseContext ctx) {
        Collection<AssignmentSegment> assignments = new LinkedList<>();
        for (AssignmentContext each : ctx.assignment()) {
            assignments.add((AssignmentSegment) visit(each));
        }
        return new SetAssignmentSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), assignments);
    }
    
    @Override
    public ASTNode visitAssignmentValues(final AssignmentValuesContext ctx) {
        List<ExpressionSegment> segments = new LinkedList<>();
        for (AssignmentValueContext each : ctx.assignmentValue()) {
            segments.add((ExpressionSegment) visit(each));
        }
        return new InsertValuesSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), segments);
    }
    
    @Override
    public ASTNode visitAssignment(final AssignmentContext ctx) {
        ColumnSegment column = (ColumnSegment) visitColumnName(ctx.columnName());
        ExpressionSegment value = (ExpressionSegment) visit(ctx.assignmentValue());
        return new AssignmentSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), column, value);
    }
    
    @Override
    public ASTNode visitAssignmentValue(final AssignmentValueContext ctx) {
        ExprContext expr = ctx.expr();
        if (null != expr) {
            return visit(expr);
        }
        return new CommonExpressionSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), ctx.getText());
    }
    
    @Override
    public ASTNode visitBlobValue(final BlobValueContext ctx) {
        return new StringLiteralValue(ctx.STRING_().getText());
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public ASTNode visitDelete(final DeleteContext ctx) {
        DeleteStatement result = new DeleteStatement();
        if (null != ctx.multipleTablesClause()) {
            result.getTables().addAll(((CollectionValue<TableSegment>) visit(ctx.multipleTablesClause())).getValue());
        } else {
            result.getTables().add((TableSegment) visit(ctx.singleTableClause()));
        }
        if (null != ctx.whereClause()) {
            result.setWhere((WhereSegment) visit(ctx.whereClause()));
        }
        result.setParametersCount(getCurrentParameterIndex());
        return result;
    }
    
    @Override
    public ASTNode visitSingleTableClause(final SingleTableClauseContext ctx) {
        TableSegment result = (TableSegment) visit(ctx.tableName());
        if (null != ctx.alias()) {
            result.setAlias((AliasSegment) visit(ctx.alias()));
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public ASTNode visitMultipleTablesClause(final MultipleTablesClauseContext ctx) {
        CollectionValue<TableSegment> result = new CollectionValue<>();
        result.combine((CollectionValue<TableSegment>) visit(ctx.multipleTableNames()));
        result.combine((CollectionValue<TableSegment>) visit(ctx.tableReferences()));
        return result;
    }
    
    @Override
    public ASTNode visitMultipleTableNames(final MultipleTableNamesContext ctx) {
        CollectionValue<TableSegment> result = new CollectionValue<>();
        for (TableNameContext each : ctx.tableName()) {
            result.getValue().add((TableSegment) visit(each));
        }
        return result;
    }
    
    @Override
    public ASTNode visitSelect(final SelectContext ctx) {
        // TODO : Unsupported for withClause.
        SelectStatement result = (SelectStatement) visit(ctx.unionClause());
        result.setParametersCount(getCurrentParameterIndex());
        return result;
    }
    
    @Override
    public ASTNode visitUnionClause(final UnionClauseContext ctx) {
        // TODO : Unsupported for union SQL.
        return visit(ctx.selectClause(0));
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public ASTNode visitSelectClause(final SelectClauseContext ctx) {
        SelectStatement result = new SelectStatement();
        result.setProjections((ProjectionsSegment) visit(ctx.projections()));
        if (null != ctx.selectSpecification()) {
            result.getProjections().setDistinctRow(isDistinct(ctx));
        }
        if (null != ctx.fromClause()) {
            result.getTables().addAll(((CollectionValue<TableSegment>) visit(ctx.fromClause())).getValue());
        }
        if (null != ctx.whereClause()) {
            result.setWhere((WhereSegment) visit(ctx.whereClause()));
        }
        if (null != ctx.groupByClause()) {
            result.setGroupBy((GroupBySegment) visit(ctx.groupByClause()));
        }
        if (null != ctx.orderByClause()) {
            result.setOrderBy((OrderBySegment) visit(ctx.orderByClause()));
        }
        if (null != ctx.limitClause()) {
            result.setLimit((LimitSegment) visit(ctx.limitClause()));
        }
        if (null != ctx.lockClause()) {
            result.setLock((LockSegment) visit(ctx.lockClause()));
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private Collection<TableSegment> getTableSegments(final Collection<TableSegment> tableSegments, final JoinedTableContext joinedTable) {
        Collection<TableSegment> result = new LinkedList<>();
        for (TableSegment tableSegment : ((CollectionValue<TableSegment>) visit(joinedTable)).getValue()) {
            if (isTable(tableSegment, tableSegments)) {
                result.add(tableSegment);
            }
        }
        return result;
    }
    
    private boolean isTable(final TableSegment owner, final Collection<TableSegment> tableSegments) {
        for (TableSegment each : tableSegments) {
            if (owner.getIdentifier().getValue().equals(each.getAlias().orNull())) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isDistinct(final SelectClauseContext ctx) {
        for (SelectSpecificationContext each : ctx.selectSpecification()) {
            if (((BooleanLiteralValue) visit(each)).getValue()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public ASTNode visitSelectSpecification(final SelectSpecificationContext ctx) {
        if (null != ctx.duplicateSpecification()) {
            return visit(ctx.duplicateSpecification());
        }
        return new BooleanLiteralValue(false);
    }
    
    @Override
    public ASTNode visitDuplicateSpecification(final DuplicateSpecificationContext ctx) {
        String text = ctx.getText();
        if ("DISTINCT".equalsIgnoreCase(text) || "DISTINCTROW".equalsIgnoreCase(text)) {
            return new BooleanLiteralValue(true);
        }
        return new BooleanLiteralValue(false);
    }
    
    @Override
    public ASTNode visitProjections(final ProjectionsContext ctx) {
        Collection<ProjectionSegment> projections = new LinkedList<>();
        if (null != ctx.unqualifiedShorthand()) {
            projections.add(
                    new ShorthandProjectionSegment(ctx.unqualifiedShorthand().getStart().getStartIndex(), ctx.unqualifiedShorthand().getStop().getStopIndex(), ctx.unqualifiedShorthand().getText()));
        }
        for (ProjectionContext each : ctx.projection()) {
            projections.add((ProjectionSegment) visit(each));
        }
        ProjectionsSegment result = new ProjectionsSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex());
        result.getProjections().addAll(projections);
        return result;
    }
    
    @Override
    public ASTNode visitProjection(final ProjectionContext ctx) {
        // FIXME: The stop index of project is the stop index of projection, instead of alias.
        if (null != ctx.qualifiedShorthand()) {
            QualifiedShorthandContext shorthand = ctx.qualifiedShorthand();
            ShorthandProjectionSegment result = new ShorthandProjectionSegment(shorthand.getStart().getStartIndex(), shorthand.getStop().getStopIndex(), shorthand.getText());
            IdentifierValue identifier = new IdentifierValue(shorthand.identifier().getText());
            result.setOwner(new TableSegment(shorthand.identifier().getStart().getStartIndex(), shorthand.identifier().getStop().getStopIndex(), identifier));
            return result;
        }
        AliasSegment alias = null == ctx.alias() ? null : (AliasSegment) visit(ctx.alias());
        if (null != ctx.columnName()) {
            ColumnSegment column = (ColumnSegment) visit(ctx.columnName());
            ColumnProjectionSegment result = new ColumnProjectionSegment(ctx.columnName().getText(), column);
            result.setAlias(alias);
            return result;
        }
        return createProjection(ctx, alias);
    }
    
    @Override
    public ASTNode visitAlias(final AliasContext ctx) {
        if (null != ctx.identifier()) {
            return new AliasSegment(ctx.start.getStartIndex(), ctx.stop.getStopIndex(), (IdentifierValue) visit(ctx.identifier()));
        }
        return new AliasSegment(ctx.start.getStartIndex(), ctx.stop.getStopIndex(), new IdentifierValue(ctx.STRING_().getText()));
    }
    
    private ASTNode createProjection(final ProjectionContext ctx, final AliasSegment alias) {
        ASTNode projection = visit(ctx.expr());
        if (projection instanceof AggregationProjectionSegment) {
            ((AggregationProjectionSegment) projection).setAlias(alias);
            return projection;
        }
        if (projection instanceof ExpressionProjectionSegment) {
            ((ExpressionProjectionSegment) projection).setAlias(alias);
            return projection;
        }
        if (projection instanceof CommonExpressionSegment) {
            CommonExpressionSegment segment = (CommonExpressionSegment) projection;
            ExpressionProjectionSegment result = new ExpressionProjectionSegment(segment.getStartIndex(), segment.getStopIndex(), segment.getText());
            result.setAlias(alias);
            return result;
        }
        // FIXME :For DISTINCT()
        if (projection instanceof ColumnSegment) {
            ExpressionProjectionSegment result = new ExpressionProjectionSegment(ctx.start.getStartIndex(), ctx.stop.getStopIndex(), ctx.getText());
            result.setAlias(alias);
            return result;
        }
        if (projection instanceof SubquerySegment) {
            return new SubquerySegment(((SubquerySegment) projection).getStartIndex(), ((SubquerySegment) projection).getStopIndex(), ((SubquerySegment) projection).getText());
        }
        LiteralExpressionSegment column = (LiteralExpressionSegment) projection;
        ExpressionProjectionSegment result = null == alias ? new ExpressionProjectionSegment(column.getStartIndex(), column.getStopIndex(), String.valueOf(column.getLiterals()))
                : new ExpressionProjectionSegment(column.getStartIndex(), ctx.alias().stop.getStopIndex(), String.valueOf(column.getLiterals()));
        result.setAlias(alias);
        return result;
    }
    
    @Override
    public ASTNode visitFromClause(final FromClauseContext ctx) {
        return visit(ctx.tableReferences());
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public ASTNode visitTableReferences(final TableReferencesContext ctx) {
        CollectionValue<TableSegment> result = new CollectionValue<>();
        for (EscapedTableReferenceContext each : ctx.escapedTableReference()) {
            result.combine((CollectionValue<TableSegment>) visit(each));
        }
        return result;
    }
    
    @Override
    public ASTNode visitEscapedTableReference(final EscapedTableReferenceContext ctx) {
        return visit(ctx.tableReference());
    }
    
    @Override
    public ASTNode visitTableReference(final TableReferenceContext ctx) {
        CollectionValue<TableSegment> result = new CollectionValue<>();
        if (null != ctx.tableFactor()) {
            ASTNode tableFactor = visit(ctx.tableFactor());
            if (tableFactor instanceof TableSegment) {
                result.getValue().add((TableSegment) tableFactor);
            }
        }
        if (null != ctx.joinedTable()) {
            for (JoinedTableContext each : ctx.joinedTable()) {
                result.getValue().addAll(getTableSegments(result.getValue(), each));
            }
        }
        return result;
    }
    
    @Override
    public ASTNode visitTableFactor(final TableFactorContext ctx) {
        if (null != ctx.tableName()) {
            TableSegment result = (TableSegment) visit(ctx.tableName());
            if (null != ctx.alias()) {
                result.setAlias((AliasSegment) visit(ctx.alias()));
            }
            return result;
        }
        if (null != ctx.tableReferences()) {
            return visit(ctx.tableReferences());
        }
        return new SubquerySegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), ctx.getText());
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public ASTNode visitJoinedTable(final JoinedTableContext ctx) {
        CollectionValue<TableSegment> result = new CollectionValue<>();
        TableSegment tableSegment = (TableSegment) visit(ctx.tableFactor());
        result.getValue().add(tableSegment);
        if (null != ctx.joinSpecification()) {
            Collection<TableSegment> tableSegments = new LinkedList<>();
            for (TableSegment each : ((CollectionValue<TableSegment>) visit(ctx.joinSpecification())).getValue()) {
                if (isTable(each, Collections.singleton(tableSegment))) {
                    tableSegments.add(each);
                }
            }
            result.getValue().addAll(tableSegments);
        }
        return result;
    }
    
    @Override
    public ASTNode visitJoinSpecification(final JoinSpecificationContext ctx) {
        CollectionValue<TableSegment> result = new CollectionValue<>();
        if (null == ctx.expr()) {
            return result;
        }
        ASTNode expr = visit(ctx.expr());
        if (expr instanceof PredicateSegment) {
            PredicateSegment predicate = (PredicateSegment) expr;
            if (predicate.getColumn().getOwner().isPresent()) {
                result.getValue().add(predicate.getColumn().getOwner().get());
            }
            if (predicate.getRightValue() instanceof ColumnSegment && ((ColumnSegment) predicate.getRightValue()).getOwner().isPresent()) {
                result.getValue().add(((ColumnSegment) predicate.getRightValue()).getOwner().get());
            }
            if (predicate.getRightValue() instanceof ColumnProjectionSegment && ((ColumnProjectionSegment) predicate.getRightValue()).getOwner().isPresent()) {
                result.getValue().add(((ColumnProjectionSegment) predicate.getRightValue()).getOwner().get());
            }
        }
        return result;
    }
    
    @Override
    public ASTNode visitWhereClause(final WhereClauseContext ctx) {
        WhereSegment result = new WhereSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex());
        ASTNode segment = visit(ctx.expr());
        if (segment instanceof OrPredicateSegment) {
            result.getAndPredicates().addAll(((OrPredicateSegment) segment).getAndPredicates());
        } else if (segment instanceof PredicateSegment) {
            AndPredicate andPredicate = new AndPredicate();
            andPredicate.getPredicates().add((PredicateSegment) segment);
            result.getAndPredicates().add(andPredicate);
        }
        return result;
    }
    
    @Override
    public ASTNode visitGroupByClause(final GroupByClauseContext ctx) {
        Collection<OrderByItemSegment> items = new LinkedList<>();
        for (OrderByItemContext each : ctx.orderByItem()) {
            items.add((OrderByItemSegment) visit(each));
        }
        return new GroupBySegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), items);
    }
    
    @Override
    public ASTNode visitLimitClause(final LimitClauseContext ctx) {
        if (null == ctx.limitOffset()) {
            return new LimitSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), null, (PaginationValueSegment) visit(ctx.limitRowCount()));
        }
        PaginationValueSegment rowCount;
        PaginationValueSegment offset;
        if (null != ctx.OFFSET()) {
            rowCount = (PaginationValueSegment) visit(ctx.limitRowCount());
            offset = (PaginationValueSegment) visit(ctx.limitOffset());
        } else {
            offset = (PaginationValueSegment) visit(ctx.limitOffset());
            rowCount = (PaginationValueSegment) visit(ctx.limitRowCount());
        }
        return new LimitSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), offset, rowCount);
    }
    
    @Override
    public ASTNode visitLimitRowCount(final LimitRowCountContext ctx) {
        if (null != ctx.numberLiterals()) {
            return new NumberLiteralLimitValueSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), ((NumberLiteralValue) visit(ctx.numberLiterals())).getValue().longValue());
        }
        return new ParameterMarkerLimitValueSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), ((ParameterMarkerValue) visit(ctx.parameterMarker())).getValue());
    }
    
    @Override
    public ASTNode visitLimitOffset(final LimitOffsetContext ctx) {
        if (null != ctx.numberLiterals()) {
            return new NumberLiteralLimitValueSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), ((NumberLiteralValue) visit(ctx.numberLiterals())).getValue().longValue());
        }
        return new ParameterMarkerLimitValueSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), ((ParameterMarkerValue) visit(ctx.parameterMarker())).getValue());
    }
    
    @Override
    public ASTNode visitLockClause(final LockClauseContext ctx) {
        return new LockSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex());
    }
}
