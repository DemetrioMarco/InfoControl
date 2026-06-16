import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StockPorSubUbicacion } from '../../../core/models/stock-report.model';
import { ReporteStockService } from '../../../core/services/reporte-stock.service';
import { SerieProductoService } from '../../../core/services/serie-producto.service';
import { SerieProductoResponse } from '../../../core/models/serie-producto.model';

@Component({
  selector: 'app-stock-report',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './stock-report.html',
  styleUrls: ['./stock-report.css']
})
export class StockReport implements OnInit {
  private reporteService = inject(ReporteStockService);
  private serieService = inject(SerieProductoService);

  rawLocationData = signal<StockPorSubUbicacion[]>([]);
  searchTerm = signal('');
  expandedKey = signal<string | null>(null); 
  seriesCache = signal<Record<string, { loading: boolean, data: SerieProductoResponse[] }>>({});

  filteredData = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    const data = this.rawLocationData();
    if (!term) return data;

    return data.filter(item =>
      item.tipoUbicacionNombre.toLowerCase().includes(term) ||
      item.ubicacionNombre.toLowerCase().includes(term) ||
      item.subUbicacionNombre.toLowerCase().includes(term) ||
      item.productos.some(p => 
        p.nombreProducto.toLowerCase().includes(term) || 
        p.codigoInterno.toLowerCase().includes(term)
      )
    );
  });

  ngOnInit() {
    this.reporteService.getPorSubUbicacion().subscribe(res => this.rawLocationData.set(res));
  }

  getCacheKey(subId: number, prodId: number): string {
    return `${subId}-${prodId}`;
  }

  toggleSeries(subId: number, prodId: number) {
    const key = this.getCacheKey(subId, prodId);
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

  exportar(id: number, nombre: string, tipo: 'pdf' | 'excel') {
    const obs = tipo === 'pdf' ? this.reporteService.exportarPdf(id) : this.reporteService.exportarExcel(id);
    obs.subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `Stock-${nombre.replace(/\s+/g, '_')}.${tipo === 'pdf' ? 'pdf' : 'xlsx'}`;
        a.click();
        window.URL.revokeObjectURL(url);
      }
    });
  }
}
