import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { SubUbicacionService } from '../../core/services/sububicacion.service';
import { UbicacionService } from '../../core/services/ubicacion.service';
import { TipoUbicacionService } from '../../core/services/tipo-ubicacion.service';
import { ReporteStockService } from '../../core/services/reporte-stock.service';
import { TomaInventarioService } from '../../core/services/toma-inventario.service';
import { ProductoService } from '../../core/services/producto.service';
import { catchError } from 'rxjs/operators';
import { forkJoin, of } from 'rxjs';
import { RegistrarConteoRequest, UpdateTomaRequest } from '../../core/models/intentory.model';
import { Role } from '../../core/models/role.enum';
import { AuthService } from '../../core/services/auth.service';
import { MovimientoInventarioService } from '../../core/services/movimiento-inventario.service';

@Component({
  selector: 'app-conteo-inventario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './conteo-inventario.html'
})
export class ConteoInventario implements OnInit {
  private subUbiService = inject(SubUbicacionService);
  private ubiService = inject(UbicacionService);
  private tipoUbiService = inject(TipoUbicacionService);
  private stockService = inject(ReporteStockService);
  private tomaService = inject(TomaInventarioService);
  private productoService = inject(ProductoService);
  private movimientoService = inject(MovimientoInventarioService);


  private readonly authService = inject(AuthService);

  user = this.authService.getUser();
  readonly Role = Role;

  hasRole(...roles: Role[]): boolean {
    return roles.includes(this.user?.rol as Role);
  }

  paso = signal<number>(2);
  loadingAjuste = signal(false);

  // Catálogos para los Selects (Programación)
  tipos = signal<any[]>([]);
  ubicaciones = signal<any[]>([]);
  subUbicacionesCombo = signal<any[]>([]);

  // Lista Global para recuperar nombres por ID en la tabla
  subUbicacionesGlobal = signal<any[]>([]);
  ubicacionesGlobal = signal<any[]>([]);


  productosReferencia = signal<any[]>([]);
  fechaProgramada = signal<string>(new Date().toISOString().split('T')[0]);

  selectedTipoId = signal<number | null>(null);
  selectedUbicId = signal<number | null>(null);
  selectedSubUbicId = signal<number | null>(null);

  tomasPendientes = signal<any[]>([]);
  tomaSeleccionada = signal<any | null>(null);
  conteosCapturados: { [key: number]: number } = {};

  ngOnInit() {
    this.cargarDatosIniciales();
  }

  cargarDatosIniciales() {
    // 1. Cargar tipos 
    this.tipoUbiService.getActivos().subscribe(res => this.tipos.set(res));

    // 2. Cargar todas las UBICACIONES (Nuevo)
    this.ubiService.getAll().subscribe(res => this.ubicacionesGlobal.set(res));

    // 3. Cargar todas las SUB-UBICACIONES
    this.subUbiService.getAll().subscribe(res => this.subUbicacionesGlobal.set(res));

    // 4. Cargar listado de tomas
    this.cargarTomasPendientes();
  }


  getNombreSubUbicacion(id: number): string {
    if (!id || this.subUbicacionesGlobal().length === 0) return 'Cargando...';
    // Forzamos comparación numérica con el +
    const encontrado = this.subUbicacionesGlobal().find(s => +s.id === +id);
    return encontrado ? encontrado.nombre : `ID: ${id}`;
  }

  getPathAnterior(subUbicacionId: number): string {
    const sub = this.subUbicacionesGlobal().find((s: any) => s.id === subUbicacionId);
    if (!sub) return '';

    const loc = this.ubicacionesGlobal().find((u: any) => u.id === sub.ubicacionId);
    const idDelTipo = loc?.tipo_ubicacion_id || loc?.tipoUbicacionId; 
    const tipo = this.tipos().find((t: any) => t.id === idDelTipo); // Puede llamarse tipoUbicacionId en tu modelo

    const nombreTipo = tipo ? tipo.nombre : '?';
    const nombreLoc = loc ? loc.nombre : '?';

    return `${nombreTipo} / ${nombreLoc}`;
  }


  userRole: Role = Role.SUPER_ADMIN;

  cargarTomasPendientes() {
    this.tomaService.getPendientes().subscribe(res => {
      this.tomasPendientes.set(res.filter((t: any) => {
        // Si es SUPER_ADMIN ve ambos estados
        if (this.hasRole(this.Role.SUPER_ADMIN)) {
          return t.estado === 'PROGRAMADA' || t.estado === 'CONTADA';
        }
        // Los demás solo ven PROGRAMADA
        return t.estado === 'PROGRAMADA';
      }));
    });
  }

  verResultadosToma(toma: any) {
    Swal.fire({ title: 'Cargando auditoría...', allowOutsideClick: false, didOpen: () => Swal.showLoading() });

    this.tomaService.getById(toma.id).subscribe({
      next: (fullToma) => {
        const solicitudesNombres = fullToma.detalles.map((d: any) =>
          this.productoService.getById(d.productoId).pipe(
            catchError(() => of({ nombre: `Producto ID: ${d.productoId}` }))
          )
        );

        forkJoin(solicitudesNombres).subscribe((productos: any) => {
          fullToma.detalles = fullToma.detalles.map((d: any, index: number) => ({
            ...d,
            productoNombre: productos[index].nombre
          }));

          this.tomaSeleccionada.set(fullToma);
          Swal.close();
          this.paso.set(4); // Salto directo al Paso 4
        });
      },
      error: () => Swal.fire('Error', 'No se pudo recuperar la toma', 'error')
    });
  }



  // --- LOGICA DE PROGRAMACION (CASO 1) ---
  onTipoChange(id: number) {
    this.selectedTipoId.set(id);
    this.ubicaciones.set([]);
    this.subUbicacionesCombo.set([]);
    if (id) {
      this.ubiService.getActivos().subscribe(res => {
        this.ubicaciones.set(res.filter((u: any) => u.tipoUbicacionId === id));
      });
    }
  }

  onUbicacionChange(id: number) {
    this.selectedUbicId.set(id);
    this.subUbicacionesCombo.set([]);
    if (id) {
      this.subUbiService.getByUbicacionActivos(id).subscribe(res => {
        this.subUbicacionesCombo.set(res);
      });
    }
  }

  onSubUbicacionChange(id: number) {
    this.selectedSubUbicId.set(id);
    if (id) {
      this.stockService.getPorSubUbicacion().subscribe(reportes => {
        const data = reportes.find(r => +r.subUbicacionId === +id);
        this.productosReferencia.set(data ? data.productos : []);
      });
    }
  }

  programarNuevaToma() {
    if (!this.selectedSubUbicId()) return;
    const payload = {
      subUbicacionId: this.selectedSubUbicId(),
      fechaProgramada: this.fechaProgramada(),
      detalles: this.productosReferencia().map(p => ({
        productoId: p.productoId,
        cantidadSistema: p.cantidad
      }))
    };
    this.tomaService.crear(payload).subscribe(() => {
      Swal.fire('Éxito', 'Programación creada', 'success');
      this.cargarTomasPendientes();
      this.paso.set(2);
    });
  }

  // --- LOGICA DE CONTEO (PASO 3) ---
  seleccionarTomaParaContar(toma: any) {
    Swal.fire({ title: 'Cargando nombres...', allowOutsideClick: false, didOpen: () => Swal.showLoading() });

    this.tomaService.getById(toma.id).subscribe({
      next: (fullToma) => {
        // Creamos un array de peticiones para obtener cada nombre de producto
        const solicitudesNombres = fullToma.detalles.map((d: any) =>
          this.productoService.getById(d.productoId).pipe(
            catchError(() => of({ nombre: `Producto ID: ${d.productoId}` }))
          )
        );

        // forkJoin espera a que todas las peticiones terminen
        forkJoin(solicitudesNombres).subscribe((productos: any) => {
          // Asignamos el nombre recuperado a cada detalle
          fullToma.detalles = fullToma.detalles.map((d: any, index: number) => ({
            ...d,
            productoNombre: productos[index].nombre
          }));

          this.tomaSeleccionada.set(fullToma);
          this.conteosCapturados = {};
          fullToma.detalles.forEach((d: any) => this.conteosCapturados[d.id] = 0);

          Swal.close();
          this.paso.set(3);
        });
      },
      error: () => Swal.fire('Error', 'No se pudo recuperar la toma', 'error')
    });
  }

  finalizarConteo() {
    const tomaActual = this.tomaSeleccionada();
    if (!tomaActual) return;

    Swal.fire({ title: 'Procesando...', allowOutsideClick: false, didOpen: () => Swal.showLoading() });

    // 1. Registrar el Conteo (PATCH)
    const conteoReq: RegistrarConteoRequest = {

      conteos: tomaActual.detalles.map((d: any) => {
        const idNumerico = Number(d.id);
        return {
          detalleId: idNumerico,
          cantidadFisica: this.conteosCapturados[idNumerico] || 0
        };
      })
    };

    this.tomaService.registrarConteo(tomaActual.id, conteoReq).subscribe({
      next: (resPatch) => {

        // 2. Actualizar Estado (PUT)
        const updateReq: UpdateTomaRequest = {
          fechaProgramada: resPatch.fechaProgramada,
          estado: 'CONTADA',
          detalles: resPatch.detalles.map((d: any) => ({
            detalleId: d.id,
            productoId: d.productoId,
            cantidadSistema: d.cantidadSistema
          }))
        };

        this.tomaService.actualizar(tomaActual.id, updateReq).subscribe({
          next: (resPut) => {
            // 3. Recuperar nombres para la vista final
            const detallesConNombre = resPut.detalles.map((d: any) => {
              const infoPrevia = tomaActual.detalles.find((p: any) => p.productoId === d.productoId);
              return {
                ...d,
                productoNombre: infoPrevia?.productoNombre || 'Producto'
              };
            });

            this.tomaSeleccionada.set({ ...resPut, detalles: detallesConNombre });
            this.cargarTomasPendientes();
            this.paso.set(4);
            Swal.close();
          },
          error: () => Swal.fire('Error', 'No se pudo actualizar el estado de la toma', 'error')
        });
      },
      error: () => Swal.fire('Error', 'No se pudo registrar el conteo', 'error')
    });
  }

  aplicarAjustes() {
  const toma = this.tomaSeleccionada();
  if (!toma) return;

  // Filtramos solo los productos que tienen diferencia
  const detallesConDiferencia = toma.detalles.filter((d: any) => d.diferencia !== 0);

  if (detallesConDiferencia.length === 0) {
    Swal.fire('Sin diferencias', 'No hay nada que ajustar, el stock coincide.', 'info');
    return;
  }

  Swal.fire({
    title: '¿Aplicar ajustes de stock?',
    text: `Se generarán ${detallesConDiferencia.length} movimientos (entradas/salidas) para igualar el sistema con el conteo físico.`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Sí, ajustar stock',
    cancelButtonText: 'Cancelar'
  }).then((result) => {
    if (result.isConfirmed) {
      this.procesarMovimientos(toma, detallesConDiferencia);
    }
  });
}

private procesarMovimientos(toma: any, detalles: any[]) {
  this.loadingAjuste.set(true);
  Swal.fire({ title: 'Procesando ajustes...', allowOutsideClick: false, didOpen: () => Swal.showLoading() });

  const peticiones = detalles.map(det => {
    const esEntrada = det.diferencia > 0;
    
    const request: any = { // Usa tu MovimientoInventarioRequest
      tipoMovimiento: esEntrada ? 'ENTRADA' : 'SALIDA',
      productoId: det.productoId,
      cantidad: Math.abs(det.diferencia),
      subUbicacionDestinoId: esEntrada ? toma.subUbicacionId : null,
      subUbicacionOrigenId: esEntrada ? null : toma.subUbicacionId,
      numeroReferencia: `ADJ-TOMA-${toma.id}`,
      motivo: 'AJUSTE POR INVENTARIO FÍSICO',
      observaciones: `Ajuste automático desde toma ID: ${toma.id}`,
      usuarioResponsableId: this.getUserId() // Asegúrate de tener esta función o usa this.user?.id
    };
    return this.movimientoService.create(request);
  });

  forkJoin(peticiones).subscribe({
    next: () => {
      // Opcional: Actualizar el estado de la toma a "FINALIZADA" para que ya no se pueda ajustar de nuevo
      const updateReq: UpdateTomaRequest = {
        ...toma,
        estado: 'FINALIZADA',
        detalles: toma.detalles.map((d: any) => ({
            detalleId: d.id,
            productoId: d.productoId,
            cantidadSistema: d.cantidadSistema
        }))
      };

      this.tomaService.actualizar(toma.id, updateReq).subscribe(() => {
        this.loadingAjuste.set(false);
        Swal.fire('Éxito', 'El stock ha sido actualizado correctamente.', 'success');
        this.cargarTomasPendientes();
        this.paso.set(2); // Regresar al listado
      });
    },
    error: () => {
      this.loadingAjuste.set(false);
      Swal.fire('Error', 'Hubo un fallo al procesar los movimientos.', 'error');
    }
  });
}

// Función auxiliar para obtener ID de usuario
getUserId(): number {
  return this.user?.id || 0;
}

}
