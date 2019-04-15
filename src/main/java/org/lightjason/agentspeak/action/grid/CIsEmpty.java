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

import cern.colt.matrix.tobject.ObjectMatrix2D;
import com.codepoetics.protonpack.StreamUtils;
import org.lightjason.agentspeak.action.IBaseAction;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.fuzzy.IFuzzyValue;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;



/**
 * check if a position within the grid is empty.
 * The action checkes the position of the gid if it is empty.
 *
 * {@code [A|B|C] = .grid/isempty(3,3, [1,1, 8,7])}
 */
public final class CIsEmpty extends IBaseAction
{
    /**
     * serial id
     */
    private static final long serialVersionUID = -2759701589082999846L;
    /**
     * action name
     */
    private static final IPath NAME = namebyclass( CIsEmpty.class, "grid" );

    @Nonnull
    @Override
    public IPath name()
    {
        return NAME;
    }

    @Nonnegative
    @Override
    public int minimalArgumentNumber()
    {
        return 1;
    }

    @Nonnull
    @Override
    public Stream<IFuzzyValue<?>> execute( final boolean p_parallel, @Nonnull final IContext p_context,
                                           @Nonnull final List<ITerm> p_argument, @Nonnull final List<ITerm> p_return )
    {
        final List<ITerm> l_arguments = CCommon.flatten( p_argument ).collect( Collectors.toList() );

        StreamUtils.windowed( l_arguments.stream().skip( 1 ), 2, 2 )
                   .map( i -> l_arguments.get( 0 ).<ObjectMatrix2D>raw().getQuick( i.get( 0 ).<Number>raw().intValue(), i.get( 1 ).<Number>raw().intValue() ) )
                   .map( Objects::isNull )
                   .forEach( i -> p_return.add( CRawTerm.of( i ) ) );

        return Stream.empty();
    }
}
