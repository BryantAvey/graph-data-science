/*
 * Copyright (c) 2017-2020 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.neo4j.graphalgo.triangle;

import org.neo4j.graphalgo.AlgoBaseProc;
import org.neo4j.graphalgo.AlgorithmFactory;
import org.neo4j.graphalgo.AlphaAlgorithmFactory;
import org.neo4j.graphalgo.api.Graph;
import org.neo4j.graphalgo.config.GraphCreateConfig;
import org.neo4j.graphalgo.core.concurrency.Pools;
import org.neo4j.graphalgo.core.utils.TerminationFlag;
import org.neo4j.graphalgo.core.utils.paged.AllocationTracker;
import org.neo4j.graphalgo.triangle.IntersectingTriangleCount.TriangleCountResult;
import org.neo4j.logging.Log;

public abstract class TriangleBaseProc<CONFIG extends TriangleCountBaseConfig>
    extends AlgoBaseProc<IntersectingTriangleCount, TriangleCountResult, CONFIG> {

    static final String DESCRIPTION =
        "Triangle counting is a community detection graph algorithm that is used to " +
        "determine the number of triangles passing through each node in the graph.";

    @Override
    protected void validateConfigs(GraphCreateConfig graphCreateConfig, CONFIG config) {
        validateIsUndirectedGraph(graphCreateConfig);
    }

    @Override
    protected AlgorithmFactory<IntersectingTriangleCount, CONFIG> algorithmFactory(
        CONFIG config) {
        return new AlphaAlgorithmFactory<IntersectingTriangleCount, CONFIG>() {
            @Override
            public IntersectingTriangleCount buildAlphaAlgo(
                Graph graph,
                CONFIG configuration,
                AllocationTracker tracker,
                Log log
            ) {
                return new IntersectingTriangleCount(
                    graph,
                    Pools.DEFAULT,
                    configuration.concurrency(),
                    AllocationTracker.create()
                )
                    .withTerminationFlag(TerminationFlag.wrap(transaction));
            }
        };
    }
}
