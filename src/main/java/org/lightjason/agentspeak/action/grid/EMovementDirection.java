/*
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the LightJason                                                #
 * # Copyright (c) 2015-19, LightJason (info@lightjason.org)                            #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU Lesser General Public License as                     #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU Lesser General Public License for more details.                                #
 * #                                                                                    #
 * # You should have received a copy of the GNU Lesser General Public License           #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

package org.lightjason.agentspeak.action.grid;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.algo.DenseDoubleAlgebra;
import cern.jet.math.tdouble.DoubleFunctions;
import org.checkerframework.checker.index.qual.Positive;

import javax.annotation.Nonnull;
import java.util.function.Function;


/**
 * direction movement calculation
 */
public enum EMovementDirection implements IMovementDirection
{
    FORWARD( 0 ),
    FORWARDLEFT( 45 ),
    LEFT( 90 ),
    BACKWARDLEFT( 135 ),
    BACKWARD( 180 ),
    BACKWARDRIGHT( 225 ),
    RIGHT( 270 ),
    FORWARDRIGHT( 315 );


    /**
     * rotation-matrix for the direction vector
     */
    private final DoubleMatrix2D m_rotation;

    /**
     * ctor
     *
     * @param p_alpha rotation of the normal-viewpoint-vector
     */
    EMovementDirection( final double p_alpha )
    {
        m_rotation = CCommon.rotationmatrix2d( Math.toRadians( p_alpha ) );
    }

    @Nonnull
    @Override
    public DoubleMatrix1D position( @Nonnull final DoubleMatrix1D p_position,
                                    @Nonnull final DoubleMatrix1D p_goalposition )
    {
        return this.position( p_position, p_goalposition, 1D,
                i -> i.assign( DoubleFunctions.div( Math.sqrt( DenseDoubleAlgebra.DEFAULT.norm2( i ) ) ) ) );
    }

    @Nonnull
    @Override
    public DoubleMatrix1D position( @Nonnull final DoubleMatrix1D p_position,
                                    @Nonnull final DoubleMatrix1D p_goalposition,
                                    @Nonnull final Number p_speed )
    {
        return this.position( p_position, p_goalposition, p_speed,
                i -> i.assign( DoubleFunctions.div( Math.sqrt( DenseDoubleAlgebra.DEFAULT.norm2( i ) ) ) ) );
    }

    @Nonnull
    @Override
    public DoubleMatrix1D position( @Nonnull final DoubleMatrix1D p_position,
                                    @Nonnull final DoubleMatrix1D p_goalposition,
                                    @Nonnull final Function<DoubleMatrix1D, DoubleMatrix1D> p_norm )
    {
        return this.position( p_position, p_goalposition, 1D, p_norm );
    }

    @Nonnull
    @Override
    public DoubleMatrix1D position( @Nonnull final DoubleMatrix1D p_position,
                                    @Nonnull final DoubleMatrix1D p_goalposition,
                                    @Nonnull @Positive final Number p_speed,
                                    @Nonnull final Function<DoubleMatrix1D, DoubleMatrix1D> p_norm )
    {
        // calculate the straight line by: current position + l * (goal position - current position)
        // normalize direction and rotate the normalized vector based on the direction
        // calculate the target position based by: current position + speed * rotate( normalize( goal position - current position ) )
        final DoubleMatrix1D l_result = p_goalposition.copy().assign( p_position, DoubleFunctions.minus );
        return DenseDoubleAlgebra.DEFAULT.mult( m_rotation, p_norm.apply( l_result ) )
                .assign( DoubleFunctions.mult( p_speed.doubleValue() ) )
                .assign( p_position, DoubleFunctions.plus );
    }
}
