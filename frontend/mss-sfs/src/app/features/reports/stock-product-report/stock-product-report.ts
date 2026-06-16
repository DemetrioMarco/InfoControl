import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReporteStockService } from '../../../core/services/reporte-stock.service';
import { SerieProductoService } from '../../../core/services/serie-producto.service';
import { StockPorSubUbicacion } from '../../../core/models/stock-report.model';
import { SerieProductoResponse } from '../../../core/models/serie-producto.model';

@Component({
  selector: 'app-stock-product-report',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './stock-product-report.html',
  styleUrls: ['./stock-product-report.css']
})
export class StockProductReport implements OnInit {
  private reporteService = inject(ReporteStockService);
  private serieService = inject(SerieProductoService);

  rawLocationData = signal<StockPorSubUbicacion[]>([]);
  searchTerm = signal('');
  
  // Estado para series
  expandedKey = signal<string | null>(null);
  seriesCache = signal<Record<string, { loading: boolean, data: SerieProductoResponse[] }>>({});

  productsSummary = computed(() => {
    const map = new Map<number, any>();
    const data = this.rawLocationData();
    
    data.forEach(loc => {
      loc.productos.forEach(prod => {
        if (!map.has(prod.productoId)) {
          map.set(prod.productoId, {
            id: prod.productoId,
            codigo: prod.codigoInterno,
            nombre: prod.nombreProducto,
            stockCalculado: 0,
            distribucion: []
          });
        }
        
        const p = map.get(prod.productoId);
        p.stockCalculado += prod.cantidad;
        p.distribucion.push({
          subUbicacionId: loc.subUbicacionId, // Importante para el servicio
          ubicacion: loc.ubicacionNombre,
          subUbicacion: loc.subUbicacionNombre,
          cantidad: prod.cantidad
        });
      });
    });

    const list = Array.from(map.values());
    const term = this.searchTerm().toLowerCase().trim();
    
    return term 
      ? list.filter(p => p.nombre.toLowerCase().includes(term) || p.codigo.toLowerCase().includes(term))
      : list;
  });

  ngOnInit() {
    this.reporteService.getPorSubUbicacion().subscribe(res => this.rawLocationData.set(res));
  }

  getCacheKey(prodId: number, subId: number): string {
    return `${prodId}-${subId}`;
  }

  toggleSeries(prodId: number, subId: number) {
    const key = this.getCacheKey(prodId, subId);
    if (this.expandedKey() === key) {
      this.expandedKey.set(null);
      return;
    }
    this.expandedKey.set(key);

    if (!this.seriesCache()[key]) {
      this.seriesCache.update(prev => ({ ...prev, [key]: { loading: true, data: [] } }));
      this.serieService.getByProductoIdAndSubUbicacionId(prodId, subId).subscribe({
        next: (series) => this.seriesCache.update(prev => ({ ...prev, [key]: { loading: false, data: series } })),
        error: () => this.seriesCache.update(prev => ({ ...prev, [key]: { loading: false, data: [] } }))
      });
    }
  }
}
