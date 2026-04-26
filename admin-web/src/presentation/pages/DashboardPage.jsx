import React, { useState, useEffect } from 'react';
import { apiClient } from '../../data/apiClient';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import TableBarIcon from '@mui/icons-material/TableBar';
import InventoryIcon from '@mui/icons-material/Inventory';
import BarChartIcon from '@mui/icons-material/BarChart';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell,
  PieChart, Pie
} from 'recharts';

import '../styles/DashboardPage.css';

const CHART_COLORS = ['#e8e0d4', '#a89882', '#7a6e60', '#5c5248', '#3d3630'];

const CustomTooltip = ({ active, payload, label }) => {
  if (!active || !payload?.length) return null;
  return (
    <div className="chart-tooltip">
      <p className="chart-tooltip-label">{label || payload[0]?.name}</p>
      {payload.map((entry, i) => (
        <p key={i} className="chart-tooltip-value" style={{ color: entry.fill || entry.color }}>
          {entry.dataKey === 'totalVendido' ? `${entry.value} uds.` : `${Number(entry.value).toFixed(2)}€`}
        </p>
      ))}
    </div>
  );
};

const DashboardPage = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const data = await apiClient.getDashboardResumen();
        setStats(data);
      } catch (err) {
        console.error('Error al cargar estadísticas:', err);
        setError(err.message);
        setStats({ ventasHoy: 0, mesasOcupadas: 0, productosBajoStock: 0, topProductos: [] });
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  if (loading) {
    return (
      <div className="dash-loader">
        <div className="dash-loader-spinner"></div>
        <span>Cargando estadísticas...</span>
      </div>
    );
  }

  const ventasHoy = stats?.ventasHoy ?? 0;
  const mesasOcupadas = stats?.mesasOcupadas ?? 0;
  const productosBajoStock = stats?.productosBajoStock ?? 0;
  const topProductos = stats?.topProductos ?? [];
  const hasProducts = topProductos.length > 0;

  return (
    <div className="dash-container">

      {error && (
        <div className="dash-error-banner">
          <span>⚠️ No se pudo conectar con el servidor — mostrando datos por defecto</span>
        </div>
      )}

      <div className="dash-kpis">
        <div className="kpi-card">
          <div className="kpi-icon" style={{ background: 'rgba(201, 168, 106, 0.12)' }}>
            <TrendingUpIcon style={{ color: '#c9a86a' }} />
          </div>
          <div className="kpi-body">
            <span className="kpi-label">Ventas de Hoy</span>
            <span className="kpi-value">€{Number(ventasHoy).toFixed(2)}</span>
          </div>
        </div>

        <div className="kpi-card">
          <div className="kpi-icon" style={{ background: 'rgba(99, 102, 241, 0.12)' }}>
            <TableBarIcon style={{ color: '#6366f1' }} />
          </div>
          <div className="kpi-body">
            <span className="kpi-label">Mesas Ocupadas</span>
            <span className="kpi-value">{mesasOcupadas}</span>
          </div>
        </div>

        <div className="kpi-card">
          <div className="kpi-icon" style={{ background: productosBajoStock > 0 ? 'rgba(248,113,113,0.12)' : 'rgba(52,211,153,0.12)' }}>
            <InventoryIcon style={{ color: productosBajoStock > 0 ? '#f87171' : '#34d399' }} />
          </div>
          <div className="kpi-body">
            <span className="kpi-label">Bajo Stock</span>
            <span className="kpi-value" style={{ color: productosBajoStock > 0 ? '#f87171' : 'inherit' }}>
              {productosBajoStock}
            </span>
            <span className="kpi-sub">productos en nivel crítico</span>
          </div>
        </div>
      </div>

      <div className="dash-charts-row">
        <div className="dash-chart-card premium-card">
          <h3 className="dash-section-title">Unidades Vendidas por Producto</h3>
          <div className="dash-chart-wrap">
            {hasProducts ? (
              <ResponsiveContainer width="100%" height={280}>
                <BarChart data={topProductos} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.04)" vertical={false} />
                  <XAxis
                    dataKey="nombre"
                    axisLine={false}
                    tickLine={false}
                    tick={{ fill: '#64748b', fontSize: 11 }}
                  />
                  <YAxis
                    axisLine={false}
                    tickLine={false}
                    tick={{ fill: '#64748b', fontSize: 11 }}
                    allowDecimals={false}
                  />
                  <Tooltip content={<CustomTooltip />} cursor={{ fill: 'rgba(255,255,255,0.03)' }} />
                  <Bar dataKey="totalVendido" name="totalVendido" radius={[6, 6, 0, 0]} barSize={36}>
                    {topProductos.map((_, i) => (
                      <Cell key={i} fill={CHART_COLORS[i % CHART_COLORS.length]} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            ) : (
              <div className="dash-empty-chart">
                <BarChartIcon style={{ fontSize: 48, opacity: 0.3 }} />
                <p>Aún no hay ventas registradas</p>
                <span>Las gráficas aparecerán cuando se registren comandas</span>
              </div>
            )}
          </div>
        </div>

        <div className="dash-chart-card premium-card">
          <h3 className="dash-section-title">Distribución de Recaudación</h3>
          <div className="dash-chart-wrap">
            {hasProducts ? (
              <ResponsiveContainer width="100%" height={280}>
                <PieChart>
                  <Pie
                    data={topProductos}
                    dataKey="recaudacion"
                    nameKey="nombre"
                    cx="50%"
                    cy="50%"
                    outerRadius={90}
                    innerRadius={50}
                    strokeWidth={0}
                    label={({ nombre, percent }) => `${nombre} ${(percent * 100).toFixed(0)}%`}
                    labelLine={false}
                  >
                    {topProductos.map((_, i) => (
                      <Cell key={i} fill={CHART_COLORS[i % CHART_COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip content={<CustomTooltip />} />
                </PieChart>
              </ResponsiveContainer>
            ) : (
              <div className="dash-empty-chart">
                <BarChartIcon style={{ fontSize: 48, opacity: 0.3 }} />
                <p>Sin datos de recaudación</p>
                <span>Cierra y paga comandas para ver la distribución</span>
              </div>
            )}
          </div>
        </div>
      </div>

      <div className="dash-ranking premium-card">
        <h3 className="dash-section-title">Top Productos Más Vendidos</h3>

        {hasProducts ? (
          <>
            <div className="rank-header">
              <span>#</span>
              <span>Producto</span>
              <span>Vendidos</span>
              <span>Recaudación</span>
            </div>

            {topProductos.map((p, i) => (
              <div key={i} className="rank-row">
                <span className="rank-pos" style={{ color: CHART_COLORS[i % CHART_COLORS.length] }}>
                  {i + 1}
                </span>
                <span className="rank-name">{p.nombre}</span>
                <span className="rank-qty">{p.totalVendido} uds.</span>
                <span className="rank-rev">{Number(p.recaudacion).toFixed(2)}€</span>
              </div>
            ))}
          </>
        ) : (
          <div className="dash-empty-table">
            <p>No hay productos vendidos aún</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default DashboardPage;
